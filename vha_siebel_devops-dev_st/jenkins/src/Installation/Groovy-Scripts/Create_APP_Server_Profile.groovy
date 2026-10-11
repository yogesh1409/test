#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Create App Server Profile")
log.info("$DeployEnvironment")



def DeployEnvironment = "${DeployEnvironment}";
//def HostName = "${ServerHost}";

EnvName="${DeployEnvironment}"
DBHost="${DBHost}" 
DBServiceName="${DBServiceName}" 
DBConnectString="${DBConnectString}" 
DBPort="${DBPort}"
AIServerHost="${AIServerHost}" 
AppServerHost="${AppServerHost}" 
GtwServerHost="${GtwServerHost}" 
SadminPwd="${Sadmin_Password}"

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def JSON_FILE_PATH = infraConfigList["JSON_FILE_PATH"]
def AIDeploy_JSON = infraConfigList["AIDeploy_JSON"]
def AIProfile_JSON = infraConfigList["AIProfile_JSON"];
def BootstrapProfile_JSON = infraConfigList["BootstrapProfile_JSON"]
def EnterpriseDeploy_JSON = infraConfigList["EnterpriseDeploy_JSON"]
def EnterpriseProfile_JSON = infraConfigList["EnterpriseProfile_JSON"]
def GatewayConfig_JSON = infraConfigList["GatewayConfig_JSON"]
def SecurityProfile_JSON = infraConfigList["SecurityProfile_JSON"]
def SiebelServerDeploy_JSON = infraConfigList["SiebelServerDeploy_JSON"]
def SiebelServerProfile_JSON = infraConfigList["SiebelServerProfile_JSON"]
def GuestcstPwd = infraConfigList["Guest_CST_Pwd"]

def utilFilepath = infraConfigList["Git_Utility_File_Home"]
def utilFileName = infraConfigList["Utility_SiebelAutomation_File"]

def HostName = AIServerHost

def generateJSONCmd= "./siebelAutomation.so GenerateProfiles "+
"${EnvName} ${DBHost} ${DBServiceName} ${DBConnectString} ${DBPort} ${AIServerHost} ${AppServerHost} ${GtwServerHost} ${SadminPwd} ${GuestcstPwd}" 
//log.info(generateJSONCmd)
//def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/AI_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"
def utilPath = utilFilepath+utilFileName
log.info("File Path : "+ utilPath)
    properties([
		discardOldBuildsProperty()
	])

//def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/AI_Install.sh  ${Log_Base_Dir} ${Siebel_Image_Home} ${Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"
node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
        
        stage ('Generate JSON Files') {
            dir("${utilFilepath}"){
                sh "chmod 755 ${utilFileName}"
                sh "${generateJSONCmd}"
                sh "ssh sbladm@${HostName} mkdir -p ${JSON_FILE_PATH}"
                sh "scp *.json sbladm@${HostName}:${JSON_FILE_PATH}"
                sh "rm ${utilFileName}"
            }
        }
        stage ('Initialise Gateway') {
            def initialiseGtwCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_Gateway_Config.sh ${AIServerHost} ${SadminPwd}"
            sh "${initialiseGtwCmd}"
        }
        stage ("Create Security Profile") {
            def createSecProfCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_Security_Profile.sh ${AIServerHost} ${SadminPwd} ${EnvName}"
            sh "${createSecProfCmd}"
        }
        stage ('Initialise Bootstrap Profile') {
            def bootstrapProfCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_BootstrapCG_Profile.sh ${AIServerHost} ${SadminPwd}"
            sh "${bootstrapProfCmd}"
        }       
        stage ("Create Enterprise Profile") {
            def createEntProfCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_Enterprise_Profile.sh ${AIServerHost} ${SadminPwd} ${EnvName}"
            sh "${createEntProfCmd}"
        }
        stage ("Create Siebel Profile") {
            def createSiebProfCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_App_Server_Profile.sh ${AIServerHost} ${SadminPwd} ${EnvName}"
            sh "${createSiebProfCmd}"
        }
        stage ("Create AI Profile") {
            def createAIProfCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_AI_Server_Profile.sh ${AIServerHost} ${SadminPwd} ${EnvName}"
            sh "${createAIProfCmd}"
        }
        
    }
}