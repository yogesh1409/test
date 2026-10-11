#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing Enterprise Patch Installation")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def WebHostsConfig = envInfo["Web"]
def AppHostsConfig = envInfo["App"]
def GtwHostsConfig = envInfo["Gtw"]

log.info(WebHostsConfig)
log.info(AppHostsConfig)
log.info(GtwHostsConfig)

def ApplyPatch = []

//ApplyPatchAI
for(AIHost in WebHostsConfig){
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


//ApplyPatchApp
for(AppHost in AppHostsConfig){
    //karthik kannan | 26-09-2019 | Modified to accomodate conditions 
    // ! AppHost.contains(GtwHostsConfig) = > APP_INSTALL_TYPE
    //   AppHost.contains(GtwHostsConfig) = > GTW_APP_INSTALL_TYPE
   if(!GtwHostsConfig.contains( AppHost)){ 
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
   }else if(GtwHostsConfig.contains(AppHost)){
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
                    string(name: 'InstallType', value: infraConfigList["GTW_APP_INSTALL_TYPE"]),             
                    [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
                    ]
            ]
        ])
   }
}

//GatewayPatch
for(AppHost in GtwHostsConfig){
    if(!AppHostsConfig.contains(AppHost)){
        ApplyPatch.push([
            "StepName": AppHost+"-ApplyPatchOnGateway", 
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
}

def FinalJobList = [
   [
    "JobName": "Patch Installation",
    "JobList": ApplyPatch
  ]
]

deploymentPipeline(
projectId : 'EnterprisePatchInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)