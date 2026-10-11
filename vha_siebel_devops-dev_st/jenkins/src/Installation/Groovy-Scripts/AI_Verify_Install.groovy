#!/usr/bin/env groovy
@Library('shared-lib') _

def DeployEnvironment = "${DeployEnvironment}";
log.info("Executing AI Verification and installation")
log.info("Host Name $ServerHost")
AIHost = "${ServerHost}"

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);

def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

DATABASE_USER_NAME_sadmin = infraConfigList["DB_SADMIN_USERNAME"]
DATABASE_PASSWORD_sadmin = "${DB_PASSWORD_sadmin}"
DATABASE_USER_NAME_siebel = infraConfigList["DB_SIEBEL_USERNAME"]
DATABASE_PASSWORD_siebel = "${DB_PASSWORD_siebel}"
TNS_Name = "${TNS_Name}"


def PreRequsiteJobs = []
def InstallAIJob = []

PreRequsiteJobs.push([
    "StepName": AIHost+"_Cert_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Certificate_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),
            string(name: 'Siebel_Dumps_Home', value: infraConfigList["Siebel_Dumps_Home"]),            
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])
/* commented for PT install
PreRequsiteJobs.push([
    "StepName": AIHost+"_DB_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("DB_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'DATABASE_USER_NAME_sadmin', value: DATABASE_USER_NAME_sadmin), 
            string(name: 'DATABASE_PASSWORD_sadmin', value: DATABASE_PASSWORD_sadmin), 
            string(name: 'DATABASE_USER_NAME_siebel', value: DATABASE_USER_NAME_siebel),
            string(name: 'DATABASE_PASSWORD_siebel', value: DATABASE_PASSWORD_siebel),            
            string(name: 'TNS_Name', value: TNS_Name ), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])
*/

PreRequsiteJobs.push([
    "StepName": AIHost+"_Network_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Network_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'Script_Base_Dir', value: infraConfigList["Script_Base_Dir"]), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

PreRequsiteJobs.push([
    "StepName": AIHost+"_Server_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Server_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'Script_Base_Dir', value: infraConfigList["Script_Base_Dir"]), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

PreRequsiteJobs.push([
    "StepName": AIHost+"_Software_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Software_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: AIHost), 
            string(name: 'Siebel_Dumps_Home', value: infraConfigList["Siebel_Dumps_Home"]),
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

InstallAIJob.push([
    "StepName": AIHost+"_Install_AI", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("AI_Install_SBL"), 
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

def FinalJobList = [
    [
  "JobName": "PreRequsteCheck",
  "JobList": PreRequsiteJobs
    ],
    [
  "JobName": "Install AI",
  "JobList": InstallAIJob
    ]
]

deploymentPipeline(
projectId : 'EnterpriseInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)