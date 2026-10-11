@Library('shared-lib') _

log.info("Executing AI Rolling Restart for ${ServerHost}")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def host = "${ServerHost}"


def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
//def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

log.info("Restart AI service $host")

def AIStartJob = []
def AIServiceCheckStopJob = []
def AIServiceCheckStartJob = []
def AIStopJob = []

AIStopJob.push([ 
  "StepName": host, 
  "StepDetails":  [
      "jobName": jobList.GetJobName("AI_Service_Stop"), 
      "propagateState": "true", 
      "quietTime": "5", 
      "waitTime": "true", 
      "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
  ]
])

AIServiceCheckStopJob.push([ 
  "StepName": host, 
  "StepDetails":  [
      "jobName": jobList.GetJobName("AI_Service_Status"), 
      "propagateState": "true", 
      "quietTime": "5", 
      "waitTime": "true", 
      "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
  ]
])

AIStartJob.push([ 
  "StepName": host, 
  "StepDetails":  [
      "jobName": jobList.GetJobName("AI_Service_Start"), 
      "propagateState": "true", 
      "quietTime": "5", 
      "waitTime": "true", 
      "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
  ]
])
AIServiceCheckStartJob.push([ 
  "StepName": host, 
  "StepDetails":  [
      "jobName": jobList.GetJobName("AI_Service_Status"), 
      "propagateState": "true", 
      "quietTime": "5", 
      "waitTime": "true", 
      "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
  ]
])

def FinalJobList = [
  [
    "JobName": "Stop AI Service",
    "JobList": AIStopJob
  ],
  [
    "JobName": "Check AI Service",
    "JobList": AIServiceCheckStopJob
  ],
  [
    "JobName": "Start AI Service",
    "JobList": AIStartJob
  ],
  [
    "JobName": "Check AI Service",
    "JobList": AIServiceCheckStartJob
  ]
]

deploymentPipeline(
projectId : 'AIServerRestart',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
