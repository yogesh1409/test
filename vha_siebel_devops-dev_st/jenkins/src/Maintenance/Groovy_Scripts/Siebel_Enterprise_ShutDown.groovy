@Library('shared-lib') _

log.info("Executing Enterprise Shutdown")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
// RestartType can have two values 
// 1. All
// 2. Specific
// when Specific is passed the list of Web, APP and Gtw needs to be passed
def restartType = "${RestartType}"
log.info("Restart Type  ${RestartType}")
//Only when restartType is Specific below values needs to be passed
def cAIServer = "${AIServer}"
def cAppServer = "${AppServer}"
def cGtwServer = "${GtwServer}"


def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname)
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def webServerList = []
def appServerList = []
def gtwServerList = []

if (restartType == "All"){
    log.info("AI Server ")
    log.info(envInfo["Web"])
    log.info("App Server ")
    log.info(envInfo["App"])
    log.info("Gtw Server ")
    log.info(envInfo["Gtw"])

    webServerList = envInfo["Web"]
    appServerList = envInfo["App"]
    gtwServerList = envInfo["Gtw"]
}else{

    log.info("AI Server " + cAIServer)
    log.info("App Server " + cAppServer)
    log.info("Gtw Server " + cGtwServer)

    webServerList.push(cAIServer)
    appServerList.push(cAppServer)
    gtwServerList.push(cGtwServer)    
}


def AIStopJob = []
def AIServiceCheckJob = []
def SESStopJob = []
def SESServiceCheckJob = []
def SiebelStopJob = []
def SiebelServiceCheckJob = []
def GtwStopJob = []
def GtwServiceCheckJob = []
def GtwContainerStopJob = []
def GtwContainerCheckJob = []

for(host in webServerList){
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
    AIServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("AI_Service_Status"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
}

for(host in appServerList){
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
    SESServiceCheckJob.push([ 
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
    SiebelServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName":jobList.GetJobName("Siebel_Service_Status"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])

}

for(host in gtwServerList){
	GtwStopJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName":jobList.GetJobName("Gateway_Service_Stop"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
    GtwServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName":jobList.GetJobName("Gateway_Service_Check"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])

    if(!appServerList.contains(host)){
        GtwContainerStopJob.push([ 
            "StepName": host, 
            "StepDetails":  [
                "jobName": jobList.GetJobName("AI_Service_Stop"), 
                "propagateState": "true", 
                "quietTime": "5", 
                "waitTime": "true", 
                "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
            ]
        ])
        GtwContainerCheckJob.push([ 
            "StepName": host, 
            "StepDetails":  [
                "jobName": jobList.GetJobName("AI_Service_Status"), 
                "propagateState": "true", 
                "quietTime": "5", 
                "waitTime": "true", 
                "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "0"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
            ]
        ])
    }
}
def FinalJobList = [
    [
  "JobName": "AI Service Stop",
  "JobList": AIStopJob
    ],
     [
  "JobName": "Check AI Service",
  "JobList": AIServiceCheckJob
    ],
     [
  "JobName": "Siebel Service Stop",
  "JobList": SiebelStopJob
    ],
     [
  "JobName": "Check Siebel Service",
  "JobList": SiebelServiceCheckJob
    ],
    [
  "JobName": "Siebel SES Stop",
  "JobList": SESStopJob
    ],
     [
  "JobName": "Check SES Service",
  "JobList": SESServiceCheckJob
    ],
    [
  "JobName": "Gatewat Service Stop",
  "JobList": GtwStopJob
    ],
    [
  "JobName": "Check Gtw Service",
  "JobList": GtwServiceCheckJob
    ],
     [
  "JobName": "Gateway Container Stop",
  "JobList": GtwContainerStopJob
    ],
     [
  "JobName": "Check Gtw Container Service",
  "JobList": GtwContainerCheckJob
    ]
]

deploymentPipeline(
projectId : 'SiebelServerShutdown',
releaseID : 'Siebel',
jobDetails : FinalJobList
)