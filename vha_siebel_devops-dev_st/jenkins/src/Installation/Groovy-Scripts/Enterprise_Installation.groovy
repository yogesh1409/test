#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing Enterprise Installation")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def DBHost = envInfo["DBHost"];
def DBServiceName = envInfo["DBServiceName"];
def DBPort = envInfo["DB_Port"];
def TNS_Name = envInfo["TNS_Name"];

def AIHost = "${AIHostName}"
def GtwHost = "${GatewayHostName}"
def AppHost = "${AppHostName}"
def Sadmin_Password = "${Sadmin_Password}"
def Siebel_Password = "${Siebel_Password}"

def OracleClientInstallJob = []
def SiebelInstallJob = []
def VerifySMCJob = []
def ApplyPatch = []
def ProfileCreation = []
def DeployServerProfiles = []
def ComponentConfig = []
def ExecutePreReq = []
def DeployNonRepoJob = []

//ExecutePreReqAI
  ExecutePreReq.push([
    "StepName": AIHost+"_PreReq", 
    "StepDetails":  [
    "jobName": jobList.GetJobName("Server_PreReq"), 
    "propagateState": "true", 
    "quietTime": "5", 
    "waitTime": "true", 
    "parameters": [        
        string(name: 'ServerHost', value: AIHost), 
        [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
        ]
      ]
  ]);

//ExecutePreReqApp
  ExecutePreReq.push([
    "StepName": AppHost+"_PreReq", 
    "StepDetails":  [
    "jobName": jobList.GetJobName("Server_PreReq"), 
    "propagateState": "true", 
    "quietTime": "5", 
    "waitTime": "true", 
    "parameters": [        
        string(name: 'ServerHost', value: AppHost), 
        [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
        ]
      ]
  ]);

if(AppHost != GatewayHostName){
    //ExecutePreReqGtw
    ExecutePreReq.push([
        "StepName": GatewayHostName+"_PreReq", 
        "StepDetails":  [
        "jobName": jobList.GetJobName("Server_PreReq"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [        
            string(name: 'ServerHost', value: GatewayHostName), 
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
        ]
    ]);
}

//Install Oracle Client
OracleClientInstallJob.push([ 
        "StepName": AIHost+"_OracleClient_Install", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Oracle_Client_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: AIHost), 
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
OracleClientInstallJob.push([ 
        "StepName": AppHost+"_OracleClient_Install", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Oracle_Client_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: AppHost),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])

if(AppHost != GatewayHostName){
    OracleClientInstallJob.push([ 
        "StepName": GatewayHostName+"_OracleClient_Install", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Oracle_Client_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: GatewayHostName),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}

//Install AI Server
SiebelInstallJob.push([
    "StepName": AIHost+"_AI_Install", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("AI_Install"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'DeployEnvironment', value: DeployEnvironment), 
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'DB_PASSWORD_sadmin', value: Sadmin_Password), 
            string(name: 'DB_PASSWORD_siebel', value: Siebel_Password),            
            string(name: 'TNS_Name', value: TNS_Name),            
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

if(AppHost != GatewayHostName){
    //Install Gateway & App Server
    SiebelInstallJob.push([
        "StepName": AppHost+"_App_Install", 
        "StepDetails":  [
            "jobName": jobList.GetJobName("APP_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: AppHost),
                string(name: 'DB_PASSWORD_sadmin', value: Sadmin_Password), 
                string(name: 'DB_PASSWORD_siebel', value: Siebel_Password),
                string(name: 'TNS_Name', value: TNS_Name),
                string(name: 'InstallType', value: infraConfigList["APP_INSTALL_TYPE"]),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])

     //Install Gateway Server
    SiebelInstallJob.push([
        "StepName": GtwHost+'_Gtw_Install', 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Gateway_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: GtwHost),             
                string(name: 'DB_PASSWORD_sadmin', value: Sadmin_Password), 
                string(name: 'DB_PASSWORD_siebel', value: Siebel_Password),
                string(name: 'TNS_Name', value: TNS_Name),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])

}else{
    //Install App Server
    SiebelInstallJob.push([
        "StepName": AppHost+'_AppGtw_Install', 
        "StepDetails":  [
            "jobName": jobList.GetJobName("APP_Install"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'ServerHost', value: AppHost),
                string(name: 'DB_PASSWORD_sadmin', value: Sadmin_Password), 
                string(name: 'DB_PASSWORD_siebel', value: Siebel_Password),
                string(name: 'TNS_Name', value: TNS_Name),
                string(name: 'InstallType', value: infraConfigList["GTW_APP_INSTALL_TYPE"]),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}

//VerifySMC
VerifySMCJob.push([
    "StepName": "VerifySMC", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Verify_SMC"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)), 
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

//ApplyPatchAI
ApplyPatch.push([
    "StepName": "ApplyPatchOnAI "+AIHost, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Apply_Patch"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost),
            string(name: 'DeployEnvironment', value: DeployEnvironment),             
            string(name: 'InstallType', value: infraConfigList["AI_INSTALL_TYPE"]),             
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

if(AppHost == GatewayHostName){

    ApplyPatch.push([
        "StepName": "ApplyPatchOnAPP "+AppHost, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Apply_Patch"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'ServerHost', value: AppHost), 
                string(name: 'DeployEnvironment', value: DeployEnvironment),                
                string(name: 'InstallType', value: infraConfigList["GTW_APP_INSTALL_TYPE"]),      //Karthik Kannan |26-09-2019 | changed to gtw_app            
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
    }
else {
    ApplyPatch.push([
        "StepName": "ApplyPatchOnAPP "+AppHost, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Apply_Patch"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'ServerHost', value: AppHost),                
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'InstallType', value: infraConfigList["APP_INSTALL_TYPE"]),                 
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])

    ApplyPatch.push([
        "StepName": "ApplyPatchOnGtw "+GtwHost, 
        "StepDetails":  [
            "jobName": jobList.GetJobName("Apply_Patch"), 
            "propagateState": "true", 
            "quietTime": "5", 
            "waitTime": "true", 
            "parameters": [
                string(name: 'ServerHost', value: GtwHost), 
                string(name: 'DeployEnvironment', value: DeployEnvironment), 
                string(name: 'InstallType', value: infraConfigList["APP_INSTALL_TYPE"]),  
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}

//EnterpriseProfileCreation
ProfileCreation.push([
    "StepName": "CreateAllProfiles", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Create_All_Profile"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'DeployEnvironment', value: DeployEnvironment),
            string(name: 'DBHost', value: DBHost),
            string(name: 'DBServiceName', value: DBServiceName),
            string(name: 'DBConnectString', value: TNS_Name),
            string(name: 'DBPort', value: DBPort),
            string(name: 'AIServerHost', value: AIHost),
            string(name: 'AppServerHost', value: AppHost),
            string(name: 'GtwServerHost', value: GtwHost),
            string(name: 'Sadmin_Password', value: Sadmin_Password),
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])


DeployServerProfiles.push([
    "StepName": "DeployAllProfiles", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Deploy_All_Profile"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'DeployEnvironment', value: DeployEnvironment),
            string(name: 'DBHost', value: DBHost),
            string(name: 'DBServiceName', value: DBServiceName),
            string(name: 'DBConnectString', value: TNS_Name),
            string(name: 'DBPort', value: DBPort),
            string(name: 'SMCServerHost', value: AIHost),
            string(name: 'AIServerHost', value: AIHost),
            string(name: 'AppServerHost', value: AppHost),
            string(name: 'GtwServerHost', value: GtwHost),
            string(name: 'Sadmin_Password', value: Sadmin_Password),
            string(name: 'DeployType', value: 'All'),
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

ComponentConfig.push([
    "StepName": "CreateComponent", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Create_Component"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'DeployEnvironment', value: DeployEnvironment),
            string(name: 'AppServerHost', value: AppHost),
            string(name: 'ServerHostName', value: AppHost),            
            string(name: 'AIServerHost', value: AIHost),            
            string(name: 'Sadmin_Password', value: Sadmin_Password),
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

//Deploy CSS Files
DeployNonRepoJob.push([
    "StepName": "Deploy CSS "+AIHost, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Deploy_CSS"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'AIServerHost', value: AIHost),                    
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

//Deploy JS Files
DeployNonRepoJob.push([
    "StepName": "Deploy JS "+AIHost, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Deploy_JS"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'AIServerHost', value: AIHost),                    
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

//Deploy Image Files
DeployNonRepoJob.push([
    "StepName": "Deploy Images "+AIHost, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Deploy_Images"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'AIServerHost', value: AIHost),                    
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

//Deploy PDF Files
DeployNonRepoJob.push([
    "StepName": "Deploy PDF "+AIHost, 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Deploy_PDF"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'AIServerHost', value: AIHost),                    
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

def FinalJobList = [
    [
        "JobName": "Server Pre Requsite",
        "JobList": ExecutePreReq
    ],
    [
  "JobName": "Oracle Client Install",
  "JobList": OracleClientInstallJob
    ],
     [
  "JobName": "Siebel Server Installation",
  "JobList": SiebelInstallJob
    ],
     [
  "JobName": "Verify SMC",
  "JobList": VerifySMCJob
    ],
     [
  "JobName": "Patch Installation",
  "JobList": ApplyPatch
    ]/*,
    [
  "JobName": "Create Server Profiles",
  "JobList": ProfileCreation
    ],
    [
  "JobName": "Deploy Servers",
  "JobList": DeployServerProfiles
    ],
    [
  "JobName": "Component Configuration",
  "JobList": ComponentConfig
    ]
    ,
    [
  "JobName": "Deploy Non-Repository",
  "JobList": DeployNonRepoJob
    ],
    [
  "JobName": "Verify Application",
  "JobList": AIServiceCheckJob
    ]*/
]

deploymentPipeline(
projectId : 'EnterpriseInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)