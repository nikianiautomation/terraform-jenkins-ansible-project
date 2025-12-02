pipeline {
    agent any
    tools {
        maven 'maven-3.9.9'  // Configured in Global Tool Configuration
    }
    environment {
        // Docker Hub credentials stored in Jenkins Credentials with ID 'dockerhub-creds'
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-creds') 
        DOCKER_HUB_REPO = 'nikithalokesh/nikithademo' 
        IMAGE_TAG = "latest"
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/nikianiautomation/spring-java-maven-project.git'
            }
        }
        stage('Maven Build & Test') {
            steps {
                try {
                 withMaven(traceability: true) {
                    sh 'mvn clean verify'
                    }
                 } catch (Exception e) {
                        echo "Maven Build failed: ${e.getMessage()}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to build failure")
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
