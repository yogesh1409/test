@Library('shared-lib') _

log.info("Executing Enterprise Start")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
//RestartType can have two values 
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

def AIStartJob = []
def AIServiceCheckJob = []
def SESStartJob = []
def SESServiceCheckJob = []
def SiebelStartJob = []
def SiebelServiceCheckJob = []
def GtwStartJob = []
def GtwServiceCheckJob = []
def GtwContainerStartJob = []
def GtwContainerCheckJob = []

for(host in webServerList){
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
    AIServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("AI_Service_Status"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
}

for(host in appServerList){
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
    SESServiceCheckJob.push([ 
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
    SiebelServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Siebel_Service_Status"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])

}

for(host in gtwServerList){
	GtwStartJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Gateway_Service_Start"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host),[$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
    GtwServiceCheckJob.push([ 
        "StepName": host, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Gateway_Service_Check"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [string(name: 'ServerHost', value: host), string(name: 'CheckFor', value: "1"), [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]]
        ]
    ])
    /*if(!appServerList.contains(host)){
        GtwContainerStartJob.push([ 
            "StepName": host, 
            "StepDetails":  [
                "jobName": jobList.GetJobName("AI_Service_Start"), 
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
    }//*/
}

def FinalJobList = [
    [
  "JobName": "Gwt Container Start",
  "JobList": GtwContainerStartJob
    ],
    [
  "JobName": "Check Gtw Container Service",
  "JobList": GtwContainerCheckJob
    ],
    [
  "JobName": "Gateway Start",
  "JobList": GtwStartJob
    ],
     [
  "JobName": "Check Gateway Service",
  "JobList": GtwServiceCheckJob
    ],
     [
  "JobName": "Siebel SES Start",
  "JobList": SESStartJob
    ],
     [
  "JobName": "Check SES Service",
  "JobList": SESServiceCheckJob
    ],
     [
  "JobName": "Siebel Service Start",
  "JobList": SiebelStartJob
    ],
     [
  "JobName": "Check Siebel Service",
  "JobList": SiebelServiceCheckJob
    ],
    [
  "JobName": "AI Service Start",
  "JobList": AIStartJob
    ],
    [
  "JobName": "Check AI Service",
  "JobList": AIServiceCheckJob
    ]
]

deploymentPipeline(
projectId : 'SiebelServerStart',
releaseID : 'Siebel',
jobDetails : FinalJobList
)