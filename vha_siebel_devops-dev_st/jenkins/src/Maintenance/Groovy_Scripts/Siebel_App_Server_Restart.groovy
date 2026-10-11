@Library('shared-lib') _

log.info("Executing App Rolling Restart for ${ServerHost}")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def host = "${ServerHost}"


def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
//def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

log.info("Restart App service $host")

def SESStopJob = []
def SESServiceCheckStartJob = []
def SiebelStopJob = []
def SiebelServiceCheckStopJob = []
def SESStartJob = []
def SESServiceCheckStopJob = []
def SiebelStartJob = []
def SiebelServiceCheckStartJob = []


SESStopJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("SES_Service_Stop"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])
SESServiceCheckStopJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName":jobList.GetJobName("SES_Service_Status"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])
SiebelStopJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName":jobList.GetJobName("Siebel_Service_Stop"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])
SiebelServiceCheckStopJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName":jobList.GetJobName("Siebel_Service_Status"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])

SESStartJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("SES_Service_Start"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
SESServiceCheckStartJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("SES_Service_Status"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])
SiebelStartJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Siebel_Service_Start"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])
SiebelServiceCheckStartJob.push([ 
    "StepName": host, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Siebel_Service_Status"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
    ]
])

def FinalJobList = [
    [
    "JobName": "Stop App Service",
    "JobList": SiebelStopJob
    ],
    [
    "JobName": "Check App Service",
    "JobList": SiebelServiceCheckStopJob
    ],
    [
    "JobName": "Start App Service",
    "JobList": SESStopJob
    ],
    [
    "JobName": "Check App Service",
    "JobList": SESServiceCheckStopJob
    ],
    [
  "JobName": "Siebel SES Start",
  "JobList": SESStartJob
    ],
     [
  "JobName": "Check SES Service",
  "JobList": SESServiceCheckStartJob
    ],
     [
  "JobName": "Siebel Service Start",
  "JobList": SiebelStartJob
    ],
     [
  "JobName": "Check Siebel Service",
  "JobList": SiebelServiceCheckStartJob
    ]
]

deploymentPipeline(
projectId : 'AppServerRestart',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
