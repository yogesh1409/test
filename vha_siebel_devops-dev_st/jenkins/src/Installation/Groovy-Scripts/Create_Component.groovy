#!/usr/bin/env groovy
@Library('shared-lib') _

log.info("Create Component Definations")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);

log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);
def envInfo = envSetup.GetEnvironmentInfo(Environmentname);

def EnvName = "${DeployEnvironment}"
def ServerHostName = "${ServerHostName}";
def APPServerHost = "${APPServerHost}"
def AIServerHost = "${AIServerHost}"
def SadminPwd = "${Sadmin_Password}"


def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")
def utilFilepath = infraConfigList["Git_Utility_File_Home"]
def compFilepath = infraConfigList["Git_Component_File_Home"]
def utilFileName = infraConfigList["Utility_SiebelAutomation_File"]
def compDefFilePath = infraConfigList["COMP_CREATE_FILE_PATH"]
def EnterpriseName = infraConfigList["SiebelEnterPrisePrefix"]+EnvName
def ComponentFileName = infraConfigList["ComponentDefinationFileName"]
def SES_Home = infraConfigList["SES_Home"]

def envType = envInfo["Type"]
def envGtwHost = envInfo["Gtw"][0]
def generateCompCmd= "./siebelAutomation.so GenerateCompDef ${ServerHostName} ${ComponentFileName}"

def parametersList = []

parametersList.push([$class: 'NodeParameterValue', name: 'SlaveName', labels: [SlaveIdentifier], nodeEligibility: [$class: 'AllNodeEligibility']])
parametersList.push(string(name: 'DeployEnvironment', value: DeployEnvironment))
parametersList.push(string(name: 'RestartType', value: 'Specific'))
parametersList.push(string(name: 'AIServer', value: AIServerHost))
parametersList.push(string(name: 'AppServer', value: APPServerHost))
parametersList.push(string(name: 'GtwServer', value: envGtwHost))                

    properties([
		discardOldBuildsProperty()
	])

node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)

        stage ('Generate Component Group Cmd File') {
            dir("${utilFilepath}"){
                sh "chmod 755 ${utilFileName}"
                sh "${generateCompCmd}"
                sh "chmod 755 comp_creation_output.cmd"
                sh "ssh sbladm@${APPServerHost} mkdir -p ${compDefFilePath}"
                sh "scp comp_creation_output.cmd sbladm@${APPServerHost}:${compDefFilePath}"
                sh "rm ${utilFileName}"
            }
            dir("${compFilepath}"){
                sh "ssh sbladm@${APPServerHost} mkdir -p ${compDefFilePath}"
                sh "scp ${envType}_comp_param.cmd sbladm@${APPServerHost}:${compDefFilePath}"
            }
        }

        stage ('Create Component Group') {
            def executeCmd = "ssh sbladm@${APPServerHost} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_Component_Groups.sh ${envGtwHost} ${EnterpriseName} ${SadminPwd} comp_creation_output.cmd ${SES_Home}"
            sh "${executeCmd}"
        }

        stage ("Restart Environment"){            
            build job: jobList.GetJobName('Enterprise_Stop'), parameters: parametersList, "propagateState": "true", "quietTime": "5", "waitTime": "true"
            build job: jobList.GetJobName('Enterprise_Start'), parameters: parametersList, "propagateState": "true", "quietTime": "5", "waitTime": "true"            
        }

        stage ('Create Component Definations') {
            def executeCmd2 = "ssh sbladm@${APPServerHost} 'bash -s' < jenkins/src/Installation/shell_scripts/Create_Component_Def.sh ${envGtwHost} ${EnterpriseName} ${SadminPwd} ${envType}_comp_param.cmd ${SES_Home}"
            sh "${executeCmd2}"
        }

        stage ("Restart Environment"){            
            build job: jobList.GetJobName('Enterprise_Stop'), parameters: parametersList, "propagateState": "true", "quietTime": "5", "waitTime": "true"
            build job: jobList.GetJobName('Enterprise_Start'), parameters: parametersList, "propagateState": "true", "quietTime": "5", "waitTime": "true"            
        }
    }
}