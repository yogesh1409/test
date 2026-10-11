@Library('shared-lib') _

log.info("Executing $InstallType Patch Rollback")
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
    log.info("Rolling back Patch for AI");
    resFileName=infraConfigList["AI_Patch_Response_File_Name"]
}else{
    log.info("Rolling back Patch for App and Gtw");
    resFileName=infraConfigList["APP_Patch_Response_File_Name"]
}

def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Patch_Rollback.sh ${InstallType}"
//def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/AI_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"

    properties([
		discardOldBuildsProperty()
	])

node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {      
        stage ('execute shell') {
            sh "pwd"            
            sh installCommand
        }
    }
}