#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Raising Log Level");
log.info("$DeployEnvironment");

def DeployEnvironment = "${DeployEnvironment}";
def Environment = "${DeployEnvironment}";
def AOM = "${AOM}";
def sadmin_passwd = "${sadmin_passwd}";
def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")
def SES_Home = infraConfigList["SES_Home"];
log.info(SES_Home);
def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);
def AppHost = envInfo["App"];
def App = AppHost[0];
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
//log.info(BUILD_USER);
//def BuildUser = BUILD_USER;
def installCommand = "ssh sbladm@${App} 'bash -s' < jenkins/src/Deployment/shell_scripts/Log_Level_Raise.sh  ${Environment} ${SES_Home} ${AOM} ${sadmin_passwd} "

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