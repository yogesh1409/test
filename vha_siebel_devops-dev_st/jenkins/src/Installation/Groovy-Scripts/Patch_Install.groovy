#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing $InstallType Patch Installation")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def HostName = "${ServerHost}";
def InstallType = "${InstallType}"

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def Log_Base_Dir = infraConfigList["Log_Base_Dir"]
def Siebel_Patch_Home = infraConfigList["Siebel_Patch_Home"];
def Response_File_Home = infraConfigList["Response_File_Home"]
def resFilepath = infraConfigList["Git_Response_File_Home"]
def resFileName = infraConfigList["APP_Patch_Response_File_Name"]

log.info("Install Type $InstallType")

if(InstallType == infraConfigList["AI_INSTALL_TYPE"]){
    log.info("Installing Patch for AI");
    resFileName=infraConfigList["AI_Patch_Response_File_Name"]
}else if (InstallType == infraConfigList["GTW_APP_INSTALL_TYPE"]){   // <Karthik Kannan |26-09-2019 | Modified to accomodate app and gtw patch
    log.info("Installing Patch for App and Gtw ");
    resFileName=infraConfigList["GTW_APP_Patch_Response_File_Name"]
}else if (infraConfigList["APP_EAI_INSTALL_TYPE"]){
    log.info("Installing Patch for App and EAI");
    resFileName=infraConfigList["APP_Patch_Response_File_Name"]
}
else{                                                              // <Karthik Kannan |26-09-2019 | Modified to accomodate gtw patch
    log.info("Installing Patch for Gtw"); 
    resFileName=infraConfigList["GTW_Patch_Response_File_Name"]
}

def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/ApplyPatch.sh ${Log_Base_Dir} ${Siebel_Patch_Home} ${Response_File_Home} ${InstallType} ${FROM_MAIL_ID} ${TO_MAIL_ID} ${resFileName}"
//def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/AI_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"
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
            sh "ssh sbladm@${HostName} 'mkdir -p ${Response_File_Home}'"  
            sh "scp ${finalPath} sbladm@${HostName}:${Response_File_Home}"
            sh installCommand
        }
    }
}