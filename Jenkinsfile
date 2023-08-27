pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk17'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}
