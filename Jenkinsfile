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

        stage('Clean old t-app image') {
            steps {
                sh 'docker stop t-app || true'
                sh 'docker remove t-app || true'
            }
        }

        stage('Run Docker Compose') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
}