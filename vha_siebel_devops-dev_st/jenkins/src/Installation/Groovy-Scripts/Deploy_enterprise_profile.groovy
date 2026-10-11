#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Deploy Enterprise Profile")
log.info("$DeployEnvironment")



def DeployEnvironment = "${DeployEnvironment}";
//def HostName = "${ServerHost}";

EnvName="${DeployEnvironment}"
DBHost="${DBHost}" 
DBServiceName="${DBServiceName}" 
DBPort="${DBPort}"
DBConnectString="${DBConnectString}" 
SMCServerHost="${SMCServerHost}" 
AIServerHost="${AIServerHost}" 
AppServerHost="${AppServerHost}" 
GtwServerHost="${GtwServerHost}" 
SadminPwd="${Sadmin_Password}"
DeployType="${DeployType}" 
 
def deployEnterpriseFlag  = false
def deployAppFlag = false
def deployAIFlag = false



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
def EnterpriseName = infraConfigList["SiebelEnterPrisePrefix"]+EnvName
def GuestcstPwd = infraConfigList["Guest_CST_Pwd"]

def utilFilepath = infraConfigList["Git_Utility_File_Home"]
def utilFileName = infraConfigList["Utility_SiebelAutomation_File"]

def HostName = AIServerHost

def generateJSONCmd= ""

if(DeployType == "All"){
    log.info("Deploying Enterprise Profile for Enterprise")
    deployEnterpriseFlag = !deployEnterpriseFlag
    deployAppFlag = !deployAppFlag
    deployAIFlag = !deployAIFlag

    generateJSONCmd= "./siebelAutomation.so GenerateProfiles "+
"${EnvName} ${DBHost} ${DBServiceName} ${DBConnectString} ${DBPort} ${AIServerHost} ${AppServerHost} ${GtwServerHost} ${SadminPwd} ${GuestcstPwd}"

}else if (DeployType == "AI"){
    log.info("Deploying AI Profile to Enterprise")
    deployAIFlag = !deployAIFlag

    generateJSONCmd= "./siebelAutomation.so GenerateDeployProf "+"${EnvName} AI ${AIServerHost}"

}else if (DeployType == "App"){
    log.info("Deploying App Profile to Enterprise")
    deployAppFlag = !deployAppFlag

    generateJSONCmd= "./siebelAutomation.so GenerateDeployProf "+"${EnvName} App ${AppServerHost}"

}


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
        stage ('Deploy Enterprise') {
           if(deployEnterpriseFlag){
                def deployGtwCmd = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Deploy_Enterprise.sh ${SMCServerHost} ${SadminPwd} ${EnterpriseName}"
                sh "${deployGtwCmd}"
           }
        }
        stage ('Deploy App Server') {
            if(deployAppFlag){
                def deployAppServer = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Deploy_App_Server.sh ${SMCServerHost} ${SadminPwd} ${AppServerHost}"
                sh "${deployAppServer}"
            }
        }
        stage ("Deploy AI Server") {
           if(deployAIFlag){
                def deployAIServer = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Deploy_AI_Server.sh ${SMCServerHost} ${SadminPwd} ${AIServerHost}"
                sh "${deployAIServer}"
           }
        }        
    }
}