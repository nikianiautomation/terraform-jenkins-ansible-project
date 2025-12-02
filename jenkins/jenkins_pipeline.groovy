pipeline {
    agent any
    tools {
        maven 'maven-3.9.9'  // Configured in Global Tool Configuration
    }
    environment {
        // Docker Hub credentials stored in Jenkins Credentials with ID 'dockerhub-creds'
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-creds')
        DOCKER_HUB_REPO = 'nikithalokesh/nikithademo'
        IMAGE_TAG = 'latest'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/nikianiautomation/spring-java-maven-project.git'
            }
        }
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    whoami
                '''
            }
        }
        stage('Maven Build & Test') {
            steps {
                script {
                    try {
                        sh 'mvn -f demo/pom.xml clean -Dmaven.test.failure.ignore=true install package'
                 } catch (Exception e) {
                        echo "Maven Build failed: ${e.getMessage()}"
                        currentBuild.result = 'FAILURE'
                        error('Stopping pipeline due to build failure')
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${DOCKER_HUB_REPO}:${IMAGE_TAG}")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-creds') {
                        dockerImage.push("${IMAGE_TAG}")
                    }
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
