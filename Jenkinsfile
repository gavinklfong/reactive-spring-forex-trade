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

        // Compile source code without running unit test
        sh "mvn clean compile -P compile"
      }

      post {
        failure {
          script {
            error "Compile failed"
          }
        }
      }
    }

    stage('Unit Test') {
      steps {
        // Run test cases with tag "UnitTest"
        sh "mvn test -P unit-test"

        // Publish Jacoco report
        step( [ $class: 'JacocoPublisher' ] )
      }

      post {
        failure {
          script {
            error "Unit Test failed"
          }
        }
      }
    }

    stage('Code Analysis') {
      steps {
        // Submit source code to sonarqube for analysis
        
        // Since Sonarqube authenticates any incoming request, please generate token in sonarqube admin panel
        // Then, configure the token as secret text in Jenkins 
        withSonarQubeEnv(credentialsId: 'sonarqube', installationName: 'sonarqube') { // You can override the credential to be used
          sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.8.0.2131:sonar'
        } 
      }

      post {
        // Abort all subsequent steps if the current step failed
        failure {
          script {
            error "Code Analysis failed"
          }
        }
      }
    }

    stage('Code Analysis - Quality Gate') {
      steps {
        // Wait for analysis result from sonarqube with 5 minutes timeout
        // This step will return error if result is NOT passed
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }

      post {
        // Abort all subsequent steps if the current step failed
        failure {
          error "Code Analysis Quality Gate NOT Passed"
        }
      }
    }
  
    stage('Integeration Test') {
      steps {
        // Run test cases with tag "IntegrationTest"
        sh "mvn test -P integration-test"
      }

      post {
        // Abort all subsequent steps if the current step failed
        failure {
          script {
            error "Integration Test failed"
          }
        }
      }
    }

    stage('End-to-End Test') {
      steps {
        // Run test cases with tag "E2ETest"
        sh "mvn verify -P e2e-test"
      }

      post {
        // Abort all subsequent steps if the current step failed
        failure {
          script {
            error "End-to-End Test failed"
          }
        }
      }
    }
  }
}
