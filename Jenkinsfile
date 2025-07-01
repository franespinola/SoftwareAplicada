#!/usr/bin/env groovy

def dockerImage

pipeline {
    agent any
    
    stages {
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('check java') {
            steps {
                sh 'java -version'
            }
        }
        
        stage('clean') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -ntp clean -P-webapp'
            }
        }
        
        stage('nohttp') {
            steps {
                sh './mvnw -ntp checkstyle:check'
            }
        }
        
        stage('install tools') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v22.15.0 -DnpmVersion=10.9.2'
            }
        }
        
        stage('npm install') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm'
            }
        }
        
        stage('backend tests') {
            steps {
                sh './mvnw -ntp verify -P-webapp'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml, target/failsafe-reports/TEST-*.xml'
                }
            }
        }
        
        stage('frontend tests') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments="run test:ci"'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/test-results/TESTS-results-jest.xml'
                }
            }
        }
        
        stage('packaging') {
            steps {
                sh './mvnw -ntp verify -P-webapp -Pprod -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('build docker image') {
            steps {
                sh './mvnw -ntp jib:dockerBuild'
            }
        }
        
        stage('publish docker') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-login', 
                    passwordVariable: 'DOCKER_REGISTRY_PWD', 
                    usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                    sh '''
                        echo $DOCKER_REGISTRY_PWD | docker login -u $DOCKER_REGISTRY_USER --password-stdin
                        docker push franespi92/task-app:latest
                        docker logout
                    '''
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
