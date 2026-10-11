@Library('shared-lib') _

log.info("Generating Watermark")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def HostName = "${SAMServerHostName}"
//def WaterMarkFilePath = "${WaterMarkFilePath}" //This will be an absolute path
def SADMINUser = "${Sadmin_User}"
def SADMINPassword = "${Sadmin_Password}"
def CONNECTIONName = "${Connection_Name}"

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

/*String lStr = WaterMarkFilePath;
GeneratedWaterMarkFileName = lStr.substring(lStr.lastIndexOf("/")); //This would have a / pre-pended to the name
GeneratedWaterMarkFileName = GeneratedWaterMarkFileName.drop(1)
log.info("GeneratedWaterMarkFileName : ${GeneratedWaterMarkFileName}")*/
def WaterMarkFilePath="";
def GeneratedWaterMarkFileName=""

Date date = new Date()
def dateStr = date.toString()
dateStr=dateStr.replaceAll("\\s","_")
dateStr=dateStr.replaceAll(":","_")

def RepoPath = envSetup.GetRepositoryPath("Code")

def SAMURL = "https://${HostName}:9001"

def generateCompCmd= "./siebelAutomation.so GenerateWaterMark ${DeployEnvironment} ${SAMURL} ${SADMINUser} ${SADMINPassword} ${CONNECTIONName}"
node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
        stage('Generate Watermark'){
            dir("${utilFilepath}"){
                sh "chmod 755 ${utilFileName}"
                sh "${generateCompCmd}"
                def fileFound = fileExists("./StatusOut.out")
                if(fileFound){
                    def fileContent = readFile "./StatusOut.out"
                    log.info(fileContent)
                    def fileLines = fileContent.split("\n")
                    WaterMarkFilePath = fileLines[1]                    
                    GeneratedWaterMarkFileName = WaterMarkFilePath.substring(WaterMarkFilePath.lastIndexOf("/")); //This would have a / pre-pended to the name
                    GeneratedWaterMarkFileName = GeneratedWaterMarkFileName.drop(1)
                    log.info("GeneratedWaterMarkFileName : ${GeneratedWaterMarkFileName}")
                }else{
                    log.info("./StatusOut.out not found")
                    currentBuild.result = 'FAILURE'
                    return
                }
            }
        }
        stage ('Create Directory'){            
            dir("${GitWaterMarkPath}"){
                sh "mkdir -p ${Environmentname}"
            }
        }
        commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetCodeBranchName(SlaveIdentifier), envSetup.GetRepositoryName('Code'), null)
        
        stage('Copy Watermark'){
            dir("${GitWaterMarkPath}/${Environmentname}"){                                                        
                sh "scp sbladm@${HostName}:${WaterMarkFilePath} ."
            }
        }
        stage ('Replace Watermark') {            
            dir("${GitWaterMarkPath}/${Environmentname}"){               
                sh "mkdir -p archive"
                sh "touch ${WaterMark_FileName}"
                sh "cp ${WaterMark_FileName} archive/Active_WaterMark_"+dateStr+"_"+BUILD_NUMBER+".txt 2>test.out"
                sh "rm test.out"
                sh "mv ${GeneratedWaterMarkFileName} ${WaterMark_FileName}"               
            }
        }
        stage ('Commit to Git'){
            dir("${GitWaterMarkPath}/${Environmentname}"){                
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
            }
        }
    }
}