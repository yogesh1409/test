#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing Add Additional App server")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL");

//This would be a comma seperated values
def AppHosts = "${AppHosts}";
def Sadmin_Password = "${Sadmin_Password}"
def Siebel_Password = "${Siebel_Password}"
def SMCServerHost = "${SMCServerHost}"
def AIHost = "${AIServerHost}"
def GtwHost = "${GtwServerHost}"
def InstallType = "${InstallType}"

def DBHost = envInfo["DBHost"];
def DBServiceName = envInfo["DBServiceName"];
def DBPort = envInfo["DB_Port"];
def TNS_Name = envInfo["TNS_Name"];
def AppHostsConfig = envInfo["App"]

def availableType = [
  "${infraConfigList["APP_INSTALL_TYPE"]}",
  "${infraConfigList["APP_EAI_INSTALL_TYPE"]}",
  "${infraConfigList["Batch_INSTALL_TYPE"]}",
  "${infraConfigList["EAI_INSTALL_TYPE"]}",
  "${infraConfigList["GTW_APP_INSTALL_TYPE"]}"
]
log.info(availableType)
log.info("Install Type : "+InstallType)
log.info(availableType.contains(InstallType))
if( !availableType.contains(InstallType)){
  log.info(InstallType+" - InstallType not allowed")
  currentBuild.result = 'FAILURE'
  return
}

log.info(AppHostsConfig);
log.info("Convert the List of AppHosts to Array")
def ListOfHosts = AppHosts.split(",");

for (i in 0..<ListOfHosts.size()) {
			ListOfHosts[i] = ListOfHosts[i].trim()
}

//check if the App supplied is the list of App seervers
//else remove the App servers that are not in the list
log.warn("Below servers are not part of Enterprise")
log.info(ListOfHosts.findAll{!AppHostsConfig.contains(it)})
ListOfHosts = ListOfHosts.findAll{AppHostsConfig.contains(it)}


if(!(ListOfHosts.size() > 0)){
  log.error("Aborting the installation as there are no App servers supplied")
  currentBuild.result = 'FAILURE'
  return
}
log.info("Continuing the Installation on below App servers")
log.info(ListOfHosts)

def ExecutePreReq = [];
def OracleClientInstallJob = [];
def SiebelInstallJob = [];
def ApplyPatch = [];
def DeployServerProfiles = [];

//ExecutePreReq
for(AppHost in ListOfHosts){
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
}

//Install Oracle Client
for(AppHost in ListOfHosts){
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
  ]);
}

//Install AI Server
for(AppHost in ListOfHosts){
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
                string(name: 'InstallType', value: InstallType),
                [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                ]
        ]
    ])
}

//ApplyPatchApp
for(AppHost in ListOfHosts){
  ApplyPatch.push([
      "StepName": AppHost+"-ApplyPatchOnApp", 
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
}

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
    "JobName": "Patch Installation",
    "JobList": ApplyPatch
  ]
]

//Deploy App Servers
for(AppHost in ListOfHosts){
  DeployServerProfiles = []
  DeployServerProfiles.push([
    "StepName": AppHost+"_DeployAllProfiles", 
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
            string(name: 'SMCServerHost', value: SMCServerHost),
            string(name: 'AIServerHost', value: AIHost),
            string(name: 'AppServerHost', value: AppHost),
            string(name: 'GtwServerHost', value: GtwHost),
            string(name: 'Sadmin_Password', value: Sadmin_Password),
            string(name: 'DeployType', value: 'App'),
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
  ])
/*Uncomment this later
FinalJobList.push(
    [
      "JobName": "Deploy Servers "+AppHost,
      "JobList": DeployServerProfiles
    ]
  )
  */
}



deploymentPipeline(
projectId : 'AdditionalAppInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
