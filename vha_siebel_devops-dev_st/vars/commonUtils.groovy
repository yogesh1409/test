#!/usr/bin/env groovy

def cleanupAndCheckOutCodeFromBranchName(branchName,repositoryName, specificPath=null) {

	    stage ('Git Checkout') {

		    //step([$class: 'WsCleanup'])

		    checkout([$class: 'GitSCM', branches: [[name: branchName]], doGenerateSubmoduleConfigurations: false,
			    extensions: [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: []]], submoduleCfg: [],
			    userRemoteConfigs: [[credentialsId: 'Github-Temp-User', url: repositoryName+'.git']]])

	    }
    }






/**
 * Push git changes to remote repo
 *
 * @param path            Path to the local git repository
 * @param branch          Branch on the remote git repository
 * @param remote          Name of the remote repository
 * @param credentialsId   Credentials with write permissions
 */
def pushGitChanges(repoPath, branch = 'dev', remote = 'origin', CommitDescr='Jenkins', Proxy='http://vfproxy.vodafone.com.au:8080') {
    stage ('Commit to Git'){

        withCredentials([usernamePassword(credentialsId: 'Github-Temp-User', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            sh("git tag -a ${JOB_NAME}_${BUILD_NUMBER} -m ${CommitDescr}")                   
            sh("git config http.proxy ${Proxy}")
            sh("git config http.sslVerify false")
            sh("git config push.default simple")
            sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${repoPath} HEAD:dev --tags")
        }   
    }
}