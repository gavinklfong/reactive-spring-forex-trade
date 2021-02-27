pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
        
        // Configure OpenJDK11 by adding JDK installation with the url to installation file
        jdk "OpenJDK11"
    }

    stages {
        stage('Build') {
            steps {
                // Get code from a GitHub repository
                git 'https://github.com/gavinklfong/reactive-spring-forex-trade.git'

                // Run Maven on a Unix agent.
                sh "mvn clean compile -Dmaven.test.skip=true"
            }

        }
        
        stage('Unit Test') {
            steps {
                sh "mvn test -Dgroups='UnitTest'"
            }
        }
        
        stage('Integeration Test') {
            steps {
                sh "mvn test -Dgroups='IntegrationTest'"
            }
        }
        
        stage('End-to-End Test') {
            steps {
                sh "mvn test -Dgroups='E2E'"
            }
        }
    }
}
