#!/usr/bin/env groovy
@Library('shared-lib') _

def DeployEnvironment = "${DeployEnvironment}";
log.info("Executing Gateway Verify and Installation")
log.info("Host Name $ServerHost")
GTWHost = "${ServerHost}"
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

DATABASE_USER_NAME_sadmin = infraConfigList["DB_SADMIN_USERNAME"]
DATABASE_PASSWORD_sadmin = "${DB_PASSWORD_sadmin}"
DATABASE_USER_NAME_siebel = infraConfigList["DB_SIEBEL_USERNAME"]
DATABASE_PASSWORD_siebel = "${DB_PASSWORD_siebel}"
TNS_Name = "${TNS_Name}"

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);


def PreRequsiteJobs = []
def InstallGTWJob = []

PreRequsiteJobs.push([
    "StepName": GTWHost+"_Cert_Verification", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Certificate_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: GTWHost), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),
            string(name: 'Siebel_Dumps_Home', value: infraConfigList["Siebel_Dumps_Home"]),            
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])
/* commented for PT installation
PreRequsiteJobs.push([
    "StepName": GTWHost+"_DB_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("DB_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: GTWHost), 
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
    "StepName": GTWHost+"_Network_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Network_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: GTWHost), 
            string(name: 'Script_Base_Dir', value: infraConfigList["Script_Base_Dir"]), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

PreRequsiteJobs.push([
    "StepName": GTWHost+"_Server_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Server_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: GTWHost), 
            string(name: 'Script_Base_Dir', value: infraConfigList["Script_Base_Dir"]), 
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

PreRequsiteJobs.push([
    "StepName": GTWHost+"_Software_Verify", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("Software_Verification"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'ServerHost', value: GTWHost), 
            string(name: 'Siebel_Dumps_Home', value: infraConfigList["Siebel_Dumps_Home"]),
            string(name: 'FROM_MAIL_ID', value: sendEmail.GetNotificationFrom(DeployEnvironment)), 
            string(name: 'TO_MAIL_ID', value: sendEmail.GetNotificationMailTo(DeployEnvironment)),           
            [$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']]
            ]
    ]
])

InstallGTWJob.push([
    "StepName": GTWHost+"_Gateway_Install", 
    "StepDetails":  [
        "jobName": jobList.GetJobName("GTW_Install_SBL"), 
        "propagateState": "true", 
        "quietTime": "5", 
        "waitTime": "true", 
        "parameters": [
            string(name: 'DeployEnvironment', value: DeployEnvironment), 
            string(name: 'ServerHost', value: GTWHost), 
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
  "JobName": "Install GTW",
  "JobList": InstallGTWJob
    ]
]

deploymentPipeline(
projectId : 'EnterpriseInstall',
releaseID : 'Siebel',
jobDetails : FinalJobList
)