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
def GtwHosts = "${GtwHosts}";
def Sadmin_Password = "${Sadmin_Password}"
def Siebel_Password = "${Siebel_Password}"
def SMCServerHost = "${SMCServerHost}"

def DBHost = envInfo["DBHost"];
def DBServiceName = envInfo["DBServiceName"];
def DBPort = envInfo["DB_Port"];
def TNS_Name = envInfo["TNS_Name"];
def GtwHostsConfig = envInfo["Gtw"]

log.info(GtwHostsConfig);
log.info("Convert the List of GtwHosts to Array")
def ListOfHosts = GtwHosts.split(",");

for (i in 0..<ListOfHosts.size()) {
			ListOfHosts[i] = ListOfHosts[i].trim()
}


//check if the App supplied is the list of Gtw seervers
//else remove the Gtw servers that are not in the list
log.warn("Below servers are not part of Enterprise")
log.info(ListOfHosts.findAll{!GtwHostsConfig.contains(it)})
ListOfHosts = ListOfHosts.findAll{GtwHostsConfig.contains(it)}


if(!(ListOfHosts.size() > 0)){
  log.error("Aborting the installation as there are no Gtw servers supplied")
  currentBuild.result = 'FAILURE'
   return
}
log.info("Continuing the Installation on below Gtw servers")
log.info(ListOfHosts)

def ExecutePreReq = [];
def OracleClientInstallJob = [];
def SiebelInstallJob = [];
def ApplyPatch = [];
def DeployServerProfiles = [];

//ExecutePreReq
for(GtwHost in ListOfHosts){
  ExecutePreReq.push([
    "StepName": GtwHost+"_PreReq", 
    "StepDetails":  [
    "jobName": jobList.GetJobName("Server_PreReq"), 
    "propagateState": "true", 
    "quietTime": "5", 
    "waitTime": "true", 
    "parameters": [        
        string(name: 'ServerHost', value: GtwHost), 
        [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
        ]
      ]
  ]);
}

//Install Oracle Client
for(GtwHost in ListOfHosts){
  OracleClientInstallJob.push([ 
      "StepName": GtwHost+"_OracleClient_Install", 
      "StepDetails":  [
          "jobName": jobList.GetJobName("Oracle_Client_Install"), 
          "propagateState": "true", 
          "quietTime": "5", 
          "waitTime": "true", 
          "parameters": [
              string(name: 'DeployEnvironment', value: DeployEnvironment), 
              string(name: 'ServerHost', value: GtwHost), 
              [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
              ]
      ]
  ]);
}

//Install Gtw Server
for(GtwHost in ListOfHosts){
     SiebelInstallJob.push([
        "StepName": GtwHost, 
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
}

//ApplyPatchGtw
for(GtwHost in ListOfHosts){
  ApplyPatch.push([
      "StepName": GtwHost+"-ApplyPatchOnAI", 
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


deploymentPipeline(
projectId : 'AdditionalGtwInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)
