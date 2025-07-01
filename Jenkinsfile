#!/usr/bin/env groovy

pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Check Java') {
            steps {
                sh 'java -version'
            }
        }
        
        stage('Clean') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -ntp clean -P-webapp'
            }
        }
        
        stage('NoHTTP Check') {
            steps {
                sh './mvnw -ntp checkstyle:check'
            }
        }
        
        stage('Install Tools') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v22.15.0 -DnpmVersion=10.9.2'
            }
        }
        
        stage('NPM Install') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm'
            }
        }
        
        stage('Backend Tests') {
            steps {
                sh './mvnw -ntp verify -P-webapp'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml, target/failsafe-reports/TEST-*.xml'
                }
            }
        }
        
        stage('Frontend Tests') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments="run test:ci"'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/test-results/TESTS-results-jest.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh './mvnw -ntp verify -P-webapp -Pprod -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Publish to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-login', 
                    passwordVariable: 'DOCKER_REGISTRY_PWD', 
                    usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                    sh './mvnw -ntp jib:build'
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
