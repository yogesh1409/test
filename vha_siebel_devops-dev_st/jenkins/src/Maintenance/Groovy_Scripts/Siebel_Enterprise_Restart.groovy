@Library('shared-lib') _

log.info("Executing Enterprise Restart")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);


def enterpriseStop = []
def enterpriseStart = []

enterpriseStop.push([ 
        "StepName": DeployEnvironment+ " Stop", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Enterprise_Stop"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true",
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment),
                string(name: 'RestartType', value: 'All'),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
        ]
    ])

    enterpriseStart.push([ 
        "StepName": DeployEnvironment+ " Start", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Enterprise_Start"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'RestartType', value: 'All'),
                string(name: 'DeployEnvironment', value: DeployEnvironment),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
        ]
    ])

    

def FinalJobList = [
    [
  "JobName": "Service Stop",
  "JobList": enterpriseStop
    ],
    [
  "JobName": "Service Start",
  "JobList": enterpriseStart
    ],
]

deploymentPipeline(
projectId : 'SiebelServerShutdown',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
