#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Generating Browser Script")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";
def sadmin_passwd = "${sadmin_passwd}";
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")
def SES_Home = infraConfigList["SES_Home"];
def AI_Home = infraConfigList["AI_Home"];
def Genb_Home = infraConfigList["Genb_Home"];

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def TNS_Name = envInfo["TNS_Name"];
def AppHost = envInfo["App"];
def AIHost = envInfo["Web"];
def AI = AIHost[0];
def App = AppHost[0];
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);



def installCommand = "ssh sbladm@${App} 'bash -s' < jenkins/src/Deployment/shell_scripts/Generate_BS.sh  ${SES_Home} ${AI_Home} ${TNS_Name} ${sadmin_passwd} ${AI} ${Genb_Home} "

    properties([
		discardOldBuildsProperty()
	])
node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
        
        stage ('execute shell') {
            sh "pwd"
            sh installCommand
        }
    }
}