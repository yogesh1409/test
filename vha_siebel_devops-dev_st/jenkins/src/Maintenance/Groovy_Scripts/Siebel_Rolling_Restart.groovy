@Library('shared-lib') _

log.info("Executing Siebel Rolling Restart")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def ServerType = "${ServerType}"

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL");

def AppAndEAIServer = "APP and EAI Server";
def AIServer = "AI Server"
def availableType = [
  "${AppAndEAIServer}",
  "${AIServer}"
];
log.info("availableType : "+availableType);
log.info("ServerType : "+ ServerType+": ");

if( !availableType.contains(ServerType)){
  log.info(ServerType+" - ServerType not allowed")
  currentBuild.result = 'FAILURE'
  return
}

RollingRestart = [];

if (ServerType == AppAndEAIServer){
    RollingRestart.push([
        "StepName": "Rolling Restart", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("App_Rolling_Restart"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [                
                string(name: 'DeployEnvironment', value: DeployEnvironment),                             
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}else{
     RollingRestart.push([
        "StepName": "Rolling Restart", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("AI_Rolling_Restart"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [                
                string(name: 'DeployEnvironment', value: DeployEnvironment),                             
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}

def FinalJobList = [
  [
    "JobName": ServerType+"-Rolling Restart",
    "JobList": RollingRestart
  ] 
]


deploymentPipeline(
projectId : 'RollingRestart',
releaseID : ServerType,
jobDetails : FinalJobList
)

