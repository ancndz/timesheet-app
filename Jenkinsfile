pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean install -Ddocker.local-registry=localhost:5000 -P prod'
            }
        }

        stage('Run Docker Compose') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
}