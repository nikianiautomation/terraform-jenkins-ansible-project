pipeline {
    agent any
    tools {
        maven 'Maven-3.9.9'  // Configured in Global Tool Configuration
        jdk 'JDK-21'         // Configured JDK 21
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
                withMaven(
                    maven: 'Maven-3.9.9',
                    jdk: 'JDK-21',
                    mavenLocalRepo: '.repo',  // Local repo caching
                    mavenOpts: '-Xmx2048m -Dmaven.repo.local=.repo',
                    options: [
                        // Maven Integration Plugin options
                        mavenLogLevel: 'INFO',
                        publisherStrategy: 'runOnly',
                        recordTestFailures: true,
                        recordTestResults: true
                    ]
                ) {
                    sh 'mvn clean verify -DskipITs'  // Full verify lifecycle
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
