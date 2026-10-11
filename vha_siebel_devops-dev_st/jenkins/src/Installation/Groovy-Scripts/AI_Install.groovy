#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing AI Installation")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def HostName = "${ServerHost}";

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def Log_Base_Dir = infraConfigList["Log_Base_Dir"]
def Siebel_Image_Home = infraConfigList["Siebel_Image_Home"];
def Response_File_Home = infraConfigList["Response_File_Home"]
def resFilepath = infraConfigList["Git_Response_File_Home"]
def resFileName = infraConfigList["AI_Response_File_Name"]
def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def AI_Response_File_Home = Response_File_Home+'/ai_base_install'

def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/AI_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${AI_Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"
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
            sh "ssh sbladm@${HostName} 'mkdir -p ${Log_Base_Dir}/AI_Install'" 
            sh "ssh sbladm@${HostName} 'mkdir -p ${AI_Response_File_Home}'" 
            sh "scp ${finalPath} sbladm@${HostName}:${AI_Response_File_Home}"
            sh installCommand
        }
    }
}