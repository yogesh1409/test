#!/usr/bin/env groovy

def GetEmailGroup(groupName){
    def EmailList = [
        "Siebel_L3_Admin" : "yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "Siebel_L3_All"   : "DL-VHA-TCS-Siebel-Onsite@vodafone.com.au,DLVHATCSSiebelOffshore@vodafone.com.au",
        "QA_Admin"        : "",
        "QA_All"          : "",
        "Siebel_L2_Admin" : "yc00550179@techmahindra.com,rl0084326@techmahindra.com",
        "Siebel_L2_All"   : "",
        "Siebel_NPE_All"  : ""
    ]

    return EmailList[groupName]
}

def GetNotificationMailTo(DeployEnvironment) {

    def EnvironmentName = envSetup.GetEnvironmentName(DeployEnvironment);  
    def SlaveIdentifier = envSetup.GetSlaveIdentifier(EnvironmentName);
    /*
    def EmailList = [        
        "Dev"       :"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "SystemTest":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "NPE"       :"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "CRR"       :"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "PerfTest01":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "PerfTest02":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "PreProd"   :"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "Training"  :"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "Production":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "EK_Production":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au",
        "NN_Production":"yajnesh.padiyar@vodafone.com.au,nakul.pv@vodafone.com.au,Vijayaraj.Murugesan@vodafone.com.au,Manisankar.Murugan@vodafone.com.au,tilak.battula@vodafone.com.au"
    ]
    */

    def EmailList = [
    "Dev"           : ["Siebel_L3_Admin"],
    "SystemTest"    : ["Siebel_L3_Admin"],
    "NPE"           : ["Siebel_L3_Admin"],
    "CRR"           : ["Siebel_L3_Admin"],
    "PerfTest01"    : ["Siebel_L3_Admin"],
    "PerfTest02"    : ["Siebel_L3_Admin"],
    "PreProd"       : ["Siebel_L3_Admin","Siebel_L2_Admin"],
    "Training"      : ["Siebel_L3_Admin"],
    "Production"    : ["Siebel_L3_Admin"],
    "EK_Production" : ["Siebel_L3_Admin"],
    "NN_Production" : ["Siebel_L3_Admin"]
    ]

    def emailListType = "yajnesh.padiyar@vodafone.com.au"
    def groupEmailList = EmailList[SlaveIdentifier]
    for(groupName in groupEmailList){
        emailListType = emailListType +","+ GetEmailGroup(groupName)
    }
    return emailListType
  
}

def GetNotificationReplyTo(DeployEnvironment) {

    def EnvironmentName = envSetup.GetEnvironmentName(DeployEnvironment);

    def SlaveIdentifier = envSetup.GetSlaveIdentifier(EnvironmentName);

    def EmailList = [
        "Dev":"sbl-devst-jenkins@vodafone.com.au",
        "SystemTest":"sbl-devst-jenkins@vodafone.com.au",
        "NPE" : "sbl-npe-jenkins@vodafone.com.au",      
        "CRR":"sbl-crr-jenkins@vodafone.com.au",
        "PerfTest01":"sbl-pt01-jenkins@vodafone.com.au",
        "PerfTest02":"sbl-pt02-jenkins@vodafone.com.au",
        "PreProd":"sbl-pre-prod-jenkins@vodafone.com.au",
        "Training":"sbl-training-jenkins@vodafone.com.au",
        "Production":"sbl-prod-jenkins@vodafone.com.au",
        "EK_Production":"sbl-prod-jenkins@vodafone.com.au",
        "NN_Production":"sbl-prod-jenkins@vodafone.com.au"
    ]

    return EmailList[SlaveIdentifier]
}

def GetNotificationFrom(DeployEnvironment) {

    def EnvironmentName = envSetup.GetEnvironmentName(DeployEnvironment);

    def SlaveIdentifier = envSetup.GetSlaveIdentifier(EnvironmentName);

     def EmailList = [
        "Dev":"sbl-devst-jenkins@vodafone.com.au",
        "SystemTest":"sbl-devst-jenkins@vodafone.com.au",
        "NPE" : "sbl-npe-jenkins@vodafone.com.au",      
        "CRR":"sbl-crr-jenkins@vodafone.com.au",
        "PerfTest01":"sbl-pt01-jenkins@vodafone.com.au",
        "PerfTest02":"sbl-pt02-jenkins@vodafone.com.au",
        "PreProd":"sbl-pre-prod-jenkins@vodafone.com.au",
        "Training":"sbl-training-jenkins@vodafone.com.au",
        "Production":"sbl-prod-jenkins@vodafone.com.au",
        "EK_Production":"sbl-prod-jenkins@vodafone.com.au",
        "NN_Production":"sbl-prod-jenkins@vodafone.com.au"
    ]

    return EmailList[SlaveIdentifier]
}


def sendJobStartedEmailNotification() {

    def mailTo = GetNotificationMailTo("${DeployEnvironment}");
    def replyTo = GetNotificationReplyTo("${DeployEnvironment}");
    def from = GetNotificationReplyTo("${DeployEnvironment}");

    def jobName = currentBuild.fullDisplayName;

    emailext body: '''${SCRIPT, template="build-started-html.template"}''',
    subject: "Siebel-${DeployEnvironment}-JOB:[Initiated]-${jobName}",
    to: "${mailTo}",
    from: "${from}",
    replyTo: "${replyTo}",
    mimeType: 'text/html',

    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
}


def sendJobFaliedEmailNotification() {

    def mailTo = GetNotificationMailTo("${DeployEnvironment}");
    def replyTo = GetNotificationReplyTo("${DeployEnvironment}");
    def from = GetNotificationReplyTo("${DeployEnvironment}");

    def jobName = currentBuild.fullDisplayName;

    emailext body: '''${SCRIPT, template="build-failed-html.template"}''',
    subject: "Siebel-${DeployEnvironment}-JOB:[Failed]-${jobName}",
    to: "${mailTo}",
    from: "${from}",
    replyTo: "${replyTo}",
    mimeType: 'text/html',

    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
}


def sendJobCompletedEmailNotification() {

    def mailTo = GetNotificationMailTo("${DeployEnvironment}");
    def replyTo = GetNotificationReplyTo("${DeployEnvironment}");
    def from = GetNotificationReplyTo("${DeployEnvironment}");

    def jobName = currentBuild.fullDisplayName;

    emailext body: '''${SCRIPT, template="build-complete-html.template"}''',
    subject: "Siebel-${DeployEnvironment}-JOB:[Completed]-${jobName}",
    to: "${mailTo}",
    from: "${from}",
    replyTo: "${replyTo}",
    mimeType: 'text/html',

    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
}

def sendGenericEmail(body, subject){
    def mailTo = GetNotificationMailTo("${DeployEnvironment}");
    def replyTo = GetNotificationReplyTo("${DeployEnvironment}");
    def from = GetNotificationReplyTo("${DeployEnvironment}");


    emailext attachLog: false, 
    body: "${body}", 
    compressLog: true, 
    subject: subject,
    from: "${from}",
    replyTo: "${replyTo}",
    to: "${mailTo}",
    mimeType : 'text/html',
    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
}

