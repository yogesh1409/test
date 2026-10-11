#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Executing Oracle Client Install")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def HostName = "${ServerHost}";

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")

def Log_Base_Dir = infraConfigList["Log_Base_Dir"]
def Ora_client_Home = infraConfigList["Ora_client_Home"]
def Response_File_Home = infraConfigList["Response_File_Home"]
def resFilepath = infraConfigList["Git_Response_File_Home"]
def GitTNSFilepath = infraConfigList["Git_TNS_NAMES_File_Home"]
def resFileName = infraConfigList["Oracle_Client_Install_Response_File_Name"]
def TNSDumpPath = infraConfigList["Siebel_Dump_TNSNames"]
def TNSNamsPath = infraConfigList["TNS_NAMES_PATH"]


def FROM_MAIL_ID = sendEmail.GetNotificationFrom(DeployEnvironment)
def TO_MAIL_ID = sendEmail.GetNotificationMailTo(DeployEnvironment)



def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def TNS_NAMES_File = Environmentname+"_tnsnames.ora"

def installCommand = "ssh sbladm@${HostName} 'bash -s' < jenkins/src/Installation/shell_scripts/Oracle_Client_Install.sh  ${Log_Base_Dir} ${Ora_client_Home} ${Response_File_Home} ${FROM_MAIL_ID} ${TO_MAIL_ID}"

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
            sh "ssh sbladm@${HostName} 'mkdir -p ${Log_Base_Dir}/Ora_Client_Install'" 
            sh "ssh sbladm@${HostName} 'mkdir -p ${Response_File_Home}'" 
            sh "scp ${finalPath} sbladm@${HostName}:${Response_File_Home}"
            sh installCommand            
        }

        stage ('Copy tnsnames.ora'){
            dir("${GitTNSFilepath}"){
                sh "chmod 755 ${TNS_NAMES_File}"                
                sh "ssh sbladm@${HostName} 'mkdir -p ${TNSDumpPath}'"
                sh "scp ${TNS_NAMES_File} sbladm@${HostName}:${TNSNamsPath}"
                //sh "ssh sbladm@${HostName} 'r ${utilFileName}'"
                sh "ssh sbladm@${HostName} 'mv ${TNSNamsPath}/${TNS_NAMES_File} ${TNSNamsPath}/tnsnames.ora'"
            }
        }
    }
}