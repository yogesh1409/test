#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing App server Install")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def HostName = "${ServerHost}";
def InstallType = "${InstallType}"

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def Log_Base_Dir = infraConfigList["Log_Base_Dir"]
def Siebel_Image_Home = infraConfigList["Siebel_Image_Home"]
def Response_File_Home = infraConfigList["Response_File_Home"]
def resFilepath = infraConfigList["Git_Response_File_Home"]
def resFileName = ""

log.info("Install Type $InstallType")

if(InstallType == infraConfigList["APP_INSTALL_TYPE"]){
    resFileName=infraConfigList["APP_Response_File_Name"]
}else if(InstallType == infraConfigList["EAI_INSTALL_TYPE"]){
    resFileName=infraConfigList["EAI_Response_File_Name"]
}else if(InstallType == infraConfigList["Batch_INSTALL_TYPE"]){
    resFileName=infraConfigList["APP_EAI_Response_File_Name"]
}else if(InstallType == infraConfigList["APP_EAI_INSTALL_TYPE"]){
    resFileName=infraConfigList["APP_EAI_Response_File_Name"]
}else if(InstallType == infraConfigList["GTW_APP_INSTALL_TYPE"]){
    resFileName=infraConfigList["GTW_APP_Response_File_Name"]
}

if(resFileName == ""){
    log.info(InstallType+" - InstallType not allowed")
    currentBuild.result = 'FAILURE'
    return
}

def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def App_Response_File_Home = Response_File_Home+'/ses_base_install'

def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/SES_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${App_Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID} ${resFileName}"

def finalPath = resFilepath+resFileName
log.info("File Path : "+ finalPath)

properties([
	discardOldBuildsProperty()
])
node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
        
        stage ('execute shell') {
            sh "pwd"
            sh "ssh sbladm@${HostName} 'mkdir -p ${Log_Base_Dir}/SES_Install'"
            sh "ssh sbladm@${HostName} 'mkdir -p ${App_Response_File_Home}'"  
            sh "scp ${finalPath} sbladm@${HostName}:${App_Response_File_Home}"
            sh installCommand
        }
    }
}