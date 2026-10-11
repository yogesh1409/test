#!/usr/bin/env groovy


def call(Map config) {
	properties([
		discardOldBuildsProperty()
	])
	def manualTrigger = true
	currentBuild.upstreamBuilds.each { b ->
	//echo "Upstream build: ${b.getFullDisplayName()}"
	manualTrigger = false
	}

	if(manualTrigger){
		sendEmail.sendJobStartedEmailNotification();
	}

	try {

		timestamps {


			log.info("JOB_NAME: ${JOB_NAME}")

			DeployEnvironment = "${DeployEnvironment}";

			Environmentname = envSetup.GetEnvironmentName(DeployEnvironment);

			SlaveIdentifier = envSetup.GetSlaveIdentifier(Environmentname);

			log.info("SlaveIdentifier: " + SlaveIdentifier );

			node(SlaveIdentifier) {

				DeployEnvironment = "${DeployEnvironment}";

				currentBuild.description = DeployEnvironment + " : " + config.projectId;

				ws( DeployEnvironment + '-' + config.projectId + '-' + JOB_NAME + '-' + BUILD_NUMBER) {


					/*commonStage.cleanupAndCheckOutCodeFromBranchName(envSetup.GetDevOpsBranchName(SlaveIdentifier),envSetup.GetRepositoryName());*/


					for (jobDetail in config.jobDetails) {

						//log.info("jobDetail " + jobDetail);

						createInfraStructureCtrl(jobDetail,DeployEnvironment, SlaveIdentifier);
					}
				}
			}
		}
	}
	catch(err){

		sendEmail.sendJobFaliedEmailNotification();
		currentBuild.result = "FAILED"
		throw err
	}

	finally {
		if(manualTrigger){
			sendEmail.sendJobCompletedEmailNotification();
		}
	}
}

def createInfraStructureCtrl(jobDetail,DeployEnvironment, SlaveIdentifier) {


    def JobName = jobDetail.JobName;
    def JobList = jobDetail.JobList;

    RequiredMSBuild = [];
    RequiredMSBuild.addAll(JobList)


		 log.printDoubleLine()
		 log.info("JobList:")
		 log.info(JobList)
		 log.printDoubleLine();
    msBuildParallel = RequiredMSBuild.collectEntries {
	["${it.StepName}" : getJobdetails(it.StepName, it.StepDetails.jobName, it.StepDetails.propagateState,
		it.StepDetails.quietTime, it.StepDetails.waitTime, it.StepDetails.parameters)]
	//["'Build ${it}'": awsUtil.executeCloudFormationTemplate(it,DeployEnvironment)]
    }
		//log.info("JobParallel:")
		//println(msBuildParallel)
//node(SlaveIdentifier){
    stage(JobName) { parallel msBuildParallel }
//}

}

def getJobdetails(stepName, jobName, propagateState, quietTime, waitTime, sParameters ){
	// log.printSingleLine()
	// log.info("GetJobDetails : "+ stepName)
	// println(jobName);
	// println(propagateState);
	// println(quietTime);
	// println(waitTime);
	// log.printSingleLine()
	return {
		//build job: jobName, propagate: "false", quietPeriod: "5", wait: "true"		
		build job: jobName, propagate: propagateState, quietPeriod: quietTime, wait: waitTime, parameters: sParameters		
	}
}
