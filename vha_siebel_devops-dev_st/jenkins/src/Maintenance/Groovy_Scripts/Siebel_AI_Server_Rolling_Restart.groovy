@Library('shared-lib') _

log.info("Executing AI Rolling Restart")
def DeployEnvironment = "${DeployEnvironment}";

log.info("$DeployEnvironment")

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

webServerList = envInfo["Web"]
def FinalJobList = []

for(host in webServerList){

  def AIServerRestart = []

  AIServerRestart.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("AI_Server_Restart"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host),string(name: 'DeployEnvironment', value: DeployEnvironment),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
  ])

  FinalJobList.push(
    [
      "JobName": "Restart-"+host,
      "JobList": AIServerRestart
    ]
  );
}

deploymentPipeline(
projectId : 'AIServersRestart',
releaseID : 'Siebel',
jobDetails : FinalJobList
)