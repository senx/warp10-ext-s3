#!/usr/bin/env groovy
//
//   Copyright 2021  SenX S.A.S.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

import hudson.model.*

pipeline {
    agent any
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '3'))
    }
    environment {
        GRADLE_CMD = "./gradlew -Psigning.gnupg.keyName=${getParam('gpgKeyName')} -PsonatypeUsername=${getParam('ossrhUsername')} -PsonatypePassword=${getParam('ossrhPassword')}"
    }
    stages {

        stage('Checkout') {
            steps {
                notifyBuild('STARTED')
                git poll: false, branch: "${getParam('gitBranch')}", url: "git@${getParam('gitHost')}:${getParam('gitOwner')}/${getParam('gitRepo')}.git"
                script {
                    VERSION = getVersion()
                }
                echo "Building ${VERSION}"
            }
        }

        stage('Build') {
            steps {
                sh '$GRADLE_CMD clean build -x test'
                archiveArtifacts allowEmptyArchive: true, artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }

        stage('Test') {
            options { retry(3) }
            steps {
                sh '$GRADLE_CMD test'
                junit allowEmptyResults: true, keepLongStdio: true, testResults: '**/build/test-results/**/*.xml'
                step([$class: 'JUnitResultArchiver', allowEmptyResults: true, keepLongStdio: true, testResults: '**/build/test-results/**/*.xml'])
            }
        }

        // stage('Deploy libs to SenX\' Nexus') {
        //     options {
        //         timeout(time: 2, unit: 'HOURS')
        //     }
        //     input {
        //         message "Should we deploy libs?"
        //     }
        //     steps {
        //         sh '$GRADLE_CMD publishMavenPublicationToNexusRepository -x test'
        //     }
        // }

        stage('Deploy libs to Maven Central') {
            options {
                timeout(time: 2, unit: 'HOURS')
            }
            input {
                message "Should we deploy libs?"
            }
            steps {
                sh '$GRADLE_CMD publishToSonatype closeAndReleaseStagingRepository'
                notifyBuild('PUBLISHED')
            }
        }
    }

    post {
        success {
            notifyBuild('SUCCESSFUL')
        }
        failure {
            notifyBuild('FAILURE')
        }
        aborted {
            notifyBuild('ABORTED')
        }
        unstable {
            notifyBuild('UNSTABLE')
        }
    }
}

void notifyBuild(String buildStatus) {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESSFUL'
    String subject = "${buildStatus}: Job ${env.JOB_NAME} [${env.BUILD_DISPLAY_NAME}]"
    String summary = "${subject} (${env.BUILD_URL})"
    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else if (buildStatus == 'PUBLISHED') {
        color = 'BLUE'
        colorCode = '#0000FF'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // Send notifications
    notifySlack(colorCode, summary, buildStatus)
}

void notifySlack(color, message, buildStatus) {
    String slackURL = getParam('slackUrl')
    String payload = "{\"username\": \"${env.JOB_NAME}\",\"attachments\":[{\"title\": \"${env.JOB_NAME} ${buildStatus}\",\"color\": \"${color}\",\"text\": \"${message}\"}]}"
    sh "curl -X POST -H 'Content-type: application/json' --data '${payload}' ${slackURL}"
}

String getParam(key) {
    return params.get(key)
}

String getVersion() {
    return sh(returnStdout: true, script: '$GRADLE_CMD --quiet versionDisplay').trim()
}
