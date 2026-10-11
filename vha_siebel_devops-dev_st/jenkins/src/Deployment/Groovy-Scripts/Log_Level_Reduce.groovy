#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Reducing Log Level");
log.info("$DeployEnvironment");

def DeployEnvironment = "${DeployEnvironment}";
def Environment = "${DeployEnvironment}";
def sadmin_passwd = "${sadmin_passwd}";
def Task_Id = "${Task_Id}";
def Mail_Id = "${Mail_Id}";
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")
def SES_Home = infraConfigList["SES_Home"];
log.info(SES_Home);
def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def AppHost = envInfo["App"];
def App = AppHost[0];
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def installCommand = "ssh sbladm@${App} 'bash -s' < jenkins/src/Deployment/shell_scripts/Log_Level_Reduce.sh  ${Environment} ${SES_Home} ${Task_Id} ${sadmin_passwd} ${Mail_Id} "

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