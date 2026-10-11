#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing Add Additional AI server")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL");


//This would be a comma seperated values
def AIHosts = "${AIHosts}";
def Sadmin_Password = "${Sadmin_Password}"
def Siebel_Password = "${Siebel_Password}"
def SMCServerHost = "${SMCServerHost}"
def AppHost = "${APPServerHost}"
def GtwHost = ""

def DBHost = envInfo["DBHost"];
def DBServiceName = envInfo["DBServiceName"];
def DBPort = envInfo["DB_Port"];
def TNS_Name = envInfo["TNS_Name"];
def AIHostsConfig = envInfo["Web"]


log.info(AIHostsConfig);
log.info("Convert the List of AIHosts to Array")
def ListOfHosts = AIHosts.split(",");

for (i in 0..<ListOfHosts.size()) {
			ListOfHosts[i] = ListOfHosts[i].trim()
}


//check if the AI supplied is the list of AI seervers
//else remove the AI servers that are not in the list
log.warn("Below servers are not part of Enterprise")
log.info(ListOfHosts.findAll{!AIHostsConfig.contains(it)})
ListOfHosts = ListOfHosts.findAll{AIHostsConfig.contains(it)}


if(!(ListOfHosts.size() > 0)){
  log.error("Aborting the installation as there are no AI servers supplied")
  currentBuild.result = 'FAILURE'
   return
}
log.info("Continuing the Installation on below AI servers")
log.info(ListOfHosts)

def ExecutePreReq = [];
def OracleClientInstallJob = [];
def SiebelInstallJob = [];
def ApplyPatch = [];
def DeployServerProfiles = [];

//ExecutePreReq
for(AIHost in ListOfHosts){
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
}

//Install Oracle Client
for(AIHost in ListOfHosts){
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
  ]);
}

//Install AI Server
for(AIHost in ListOfHosts){
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
}

//ApplyPatchAI
for(AIHost in ListOfHosts){
  ApplyPatch.push([
      "StepName": AIHost+"-ApplyPatchOnAI", 
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

//Deploy AI Servers
for(AIHost in ListOfHosts){
  DeployServerProfiles = []
  DeployServerProfiles.push([
    "StepName": AIHost+"_DeployAllProfiles", 
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
            string(name: 'DeployType', value: 'AI'),
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
  ])
  /* Uncomment it later
FinalJobList.push(
    [
      "JobName": "Deploy Servers "+AIHost,
      "JobList": DeployServerProfiles
    ]
  )
  */
}




deploymentPipeline(
projectId : 'AdditionalAIInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
