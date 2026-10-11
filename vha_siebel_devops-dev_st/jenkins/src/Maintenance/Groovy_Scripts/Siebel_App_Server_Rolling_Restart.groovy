@Library('shared-lib') _

log.info("Executing App Rolling Restart")
def DeployEnvironment = "${DeployEnvironment}";
log.info("$DeployEnvironment")

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

appServerList = envInfo["App"]
def FinalJobList = []

for(host in appServerList){

  def AppServerRestart = []

  AppServerRestart.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("App_Server_Restart"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host),string(name: 'DeployEnvironment', value: DeployEnvironment),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
  ])

  FinalJobList.push(
    [
      "JobName": "Restart-"+host,
      "JobList": AppServerRestart
    ]
  );
}

deploymentPipeline(
projectId : 'AppServersRestart',
releaseID : 'Siebel',
jobDetails : FinalJobList
)