@Library('shared-lib') _

log.info("Deploy IRR Package")
log.info("$DeployEnvironment")

def DeployEnvironment = "${DeployEnvironment}";

def Sam_UserName = "${Sam_UserName}"
def Sam_Password = "${Sam_Password}"
def SamHostName = "${SamHostName}"
def Siebel_UserName = "${Siebel_UserName}"
def Siebel_Password = "${Siebel_Password}"
def PlanName = "${Plan_Name}"
def DestinationEnv = "${DestinationEnvironment}"
def PackageFileName = "${PackageFileName}"

def Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);
log.info(Environmentname);
def SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

def DestinationEnvironmentname = envSetup.GetEnvironmentName(DestinationEnv);
log.info("Destination Environment : "+DestinationEnvironmentname)
def DestinationSlaveIdentifier = envSetup.GetSlaveIdentifier(DestinationEnvironmentname)

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

def SamURL = "https://${SamHostName}:9001"

def generateCompCmd = "./siebelAutomation.so ImportRRPackge ${SamURL} ${Sam_UserName} ${Sam_Password} ${PlanName} ${Siebel_UserName} ${Siebel_Password} ${PackageFileName}"

node(SlaveIdentifier) {
    ws( DeployEnvironment + '-' + JOB_NAME + '-' + BUILD_NUMBER ) {
        stage("Download IRR Package"){
            commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetCodeBranchName(DestinationSlaveIdentifier), envSetup.GetRepositoryName('Code'), null)
            dir("${Git_IRR_Folder}/${DestinationEnvironmentname}"){
                def fileFound = fileExists("./${PackageFileName}")
                if(fileFound){
                    log.info("Pacakge ${PackageFileName} found.")
                }
                else{
                    log.info("${PackageFileName} not found")
                    currentBuild.result = 'FAILURE'
                    return
                }
            }
        }
        stage("Stage IRR Package"){
            dir("${Git_IRR_Folder}/${DestinationEnvironmentname}"){
                sh "scp ${PackageFileName} sbladm@${SamHostName}:${SAM_File_Path}"
            }
        }
        stage("Trigger IRR Import"){
            commonUtils.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier), envSetup.GetRepositoryName('DevOps'), null)
             dir("${utilFilepath}"){
                sh "chmod 755 ${utilFileName}"
                sh "${generateCompCmd}"                
            }
        }
        stage("Send Siebel Notification"){
            sendEmail.sendGenericEmail("${PackageFileName} deployment completed on ${DestinationEnvironmentname}. Proceed with sanity.", "${DestinationEnv}|IRR is Deployed")
        }
    }
}