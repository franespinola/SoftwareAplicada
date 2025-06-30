#!/usr/bin/env groovy

def dockerImage

node {
    stage('checkout') {
        checkout scm
    }

    gitlabCommitStatus(name: 'build') {
        docker.image('jhipster/jhipster:v8.1.0').inside('-u jhipster --tmpfs /.cache --tmpfs /.npm') {
            stage('check java') {
                sh "java -version"
            }

            stage('clean') {
                sh "chmod +x mvnw"
                sh "./mvnw -ntp clean -P-webapp"
            }

            stage('nohttp') {
                sh "./mvnw -ntp checkstyle:check"
            }

            stage('install tools') {
                sh "./mvnw -ntp com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v18.17.1 -DnpmVersion=9.6.7"
            }

            stage('npm install') {
                sh "./mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm"
            }

            stage('backend tests') {
                try {
                    sh "./mvnw -ntp verify -P-webapp"
                } catch(err) {
                    throw err
                } finally {
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml, target/failsafe-reports/TEST-*.xml'
                }
            }

            stage('frontend tests') {
                try {
                    sh "./mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments='run test:ci'"
                } catch(err) {
                    throw err
                } finally {
                    publishTestResults testResultsPattern: 'target/test-results/TESTS-results-jest.xml'
                }
            }

            stage('packaging') {
                sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }

            stage('quality analysis') {
                withSonarQubeEnv('Sonar') {
                    sh "./mvnw -ntp initialize sonar:sonar"
                }
            }

            // STAGE ADICIONAL PARA DOCKERHUB
            stage('publish docker') {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-login', 
                    passwordVariable: 'DOCKER_REGISTRY_PWD', 
                    usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                    sh "./mvnw -ntp jib:build"
                }
            }
        }
    }
}
