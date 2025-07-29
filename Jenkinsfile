pipeline {
    agent any
 
    environment {
        BITBUCKET_REPO = 'https://sandeepreddy04-admin@bitbucket.org/sandeepreddy04/bloggingwebsite.git'
        BRANCH = 'master'
        CREDENTIALS_ID = 'bdcdc521-1924-4aaa-b691-98eaad9af7ef'
        CATALINA_HOME = 'C:\\mysoftware\\apache-tomcat-11.0.9'
        WAR_FILE = 'BloggingWebsite-0.0.1-SNAPSHOT.war'
    }
 
    stages {
        stage('Checkout Code') {
            steps {
                git branch: "${BRANCH}", credentialsId: "${CREDENTIALS_ID}", url: "${BITBUCKET_REPO}"
            }
        }
 
        stage('Build') {
            steps {
                echo "Building the application with Maven..."
                bat 'mvn package'
            }
        }
 
        stage('Test') {
            steps {
                echo "Running tests..."
                bat 'mvn test'
            }
        }
        stage('Deploy WAR to Tomcat') {
            steps {
                echo "Deploying WAR to Tomcat..."
                bat 'copy /Y target\\%WAR_FILE% "%CATALINA_HOME%\\webapps\\%WAR_FILE%"'
            }
        }
    }
 
    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}