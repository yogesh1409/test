@Library('shared-lib') _

log.info("Generating IRR Package")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def MasterDevUser = "${MasterDev_User}"
def MasterDevPassword = "${MasterDev_Password}"
def PlanName = "${Plan_Name}"
def DestinationEnv = "${DestinationEnvironment}"
def PackageTag = "${PackageTag}"

def DestinationEnvironmentname = envSetup.GetEnvironmentName(DestinationEnv);
log.info("Destination Environment : "+DestinationEnvironmentname)
def DestinationSlaveIdentifier = envSetup.GetSlaveIdentifier(DestinationEnvironmentname)

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def infraConfigList = infraConfig.GetInfraInstallConfig("", "ALL")
def GitWaterMarkPath = infraConfigList["Git_WaterMark_Path"]
def WaterMark_FileName = infraConfigList["WaterMark_FileName"]
def HTTP_Proxy = infraConfigList["HTTP_Proxy"]
def HTTPS_Proxy = infraConfigList["HTTPS_Proxy"]
def utilFilepath = infraConfigList["Git_Utility_File_Home"]
def utilFileName = infraConfigList["Utility_SiebelAutomation_File"]
def SAM_File_Path = infraConfigList["SAM_File_Path"]
def MasterDev_Host = infraConfigList["MasterDev_Host"]
def Git_IRR_Folder = infraConfigList["Git_IRR_Folder"]

def WaterMarkFileNameJenkins = WaterMark_FileName.reverse().drop(4).reverse()+"-${PackageTag}-${BUILD_NUMBER}.txt"
def PackageName = "${DestinationEnv}-IRR-${PackageTag}-${BUILD_NUMBER}.zip"

def MasterDevURL = "https://${MasterDev_Host}:9001"
def generateCompCmd= "./siebelAutomation.so GenerateIRRPackge ${MasterDevURL} ${MasterDevUser} ${MasterDevPassword} ${PlanName} ${WaterMarkFileNameJenkins} ${PackageName}"

properties([
		discardOldBuildsProperty()
	])

node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        stage("Download Watermark"){
            commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetCodeBranchName(DestinationSlaveIdentifier), envSetup.GetRepositoryName('Code'), null)
            dir("${GitWaterMarkPath}/${DestinationEnvironmentname}"){                       
                def fileFound = fileExists("./${WaterMark_FileName}")
                if(fileFound){
                    sh "cp ./${WaterMark_FileName} ../../."
                }else{
                    log.info("${WaterMark_FileName} not found")
                    currentBuild.result = 'FAILURE'
                    return
                }
            }
        }
        stage("Place Watermark"){
            sh "mv ${WaterMark_FileName} ${WaterMarkFileNameJenkins}"
            sh "scp ${WaterMarkFileNameJenkins} sbladm@${MasterDev_Host}:${SAM_File_Path}"
        }
        stage("Trigger IRR Package"){
            commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
             dir("${utilFilepath}"){
                sh "chmod 755 ${utilFileName}"
                sh "${generateCompCmd}"                
            }
        }
        stage("Get IRR Package"){
            dir("${Git_IRR_Folder}"){
                sh "mkdir -p ${DestinationEnvironmentname}"
            }
            dir("${Git_IRR_Folder}/${DestinationEnvironmentname}"){
                sh "scp sbladm@${MasterDev_Host}:${SAM_File_Path}${PackageName} ."                
            }
        }
        stage("Commit to Git"){
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetCodeBranchName(DestinationSlaveIdentifier), envSetup.GetRepositoryName('Code'), null)
            dir("${Git_IRR_Folder}/${DestinationEnvironmentname}"){               
                def fileFound = fileExists("./${PackageName}")
                if(fileFound){
                    sh "git add *"                
                    withCredentials([usernamePassword(credentialsId: 'Github-Temp-User', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        sh("git tag -a ${JOB_NAME}_${BUILD_NUMBER} -m 'Jenkins'")
                        sh("git config http.proxy ${HTTP_Proxy}")
                        //sh("git config http.proxy ${HTTPS_Proxy}")
                        sh('git config http.sslVerify false')
                        sh('git config push.default simple')
                        sh('git commit -m ${JOB_NAME}_${BUILD_NUMBER}')                   
                        sh('git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/VodafoneAustralia/vha_siebel_crm.git HEAD:dev --tags')
                    }
                }else{
                    log.info("${PackageName} not found")
                    currentBuild.result = 'FAILURE'
                    return
                }
            }
        }
        stage("Send Release Notification"){
            sendEmail.sendGenericEmail("${PackageName} is staged for deployment. Proceed with deployment.", "${DestinationEnv}|IRR is Staged for Deployement")
        }
    }
}