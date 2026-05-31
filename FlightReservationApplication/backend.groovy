pipeline {
    agent any

    stages {
        stage('pull') {
            steps {
                git branch: 'main', url: 'https://github.com/Abhipatel2578/flight-reservation-app11.git'
            }
        }
        stage('build') {
            steps {
                sh '''
                cd FlightReservationApplication
                mvn clean package'''
            }
        }
        stage('test') {
            steps {
                sh '''
                 cd FlightReservationApplication
                mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                -Dsonar.projectKey=Flight \
                -Dsonar.projectName='Flight' \
                -Dsonar.host.url=http://51.44.178.20:9000 \
                -Dsonar.token=sqp_14872e41e80b058a3017eb22bf67444aa6375f7d'''
            }
        }
                stage('Docker-build'){
            steps{
                withDockerRegistry(credentialsId: 'docker', url: 'https://index.docker.io/v1/')
                {
                sh '''
                    cd FlightReservationApplication
                    docker build -t abhi2578/flightreservation-new:latest .
                    docker push abhi2578/flightreservation-new:latest
                    docker rmi abhi2578/flightreservation-new:latest
                '''
               }
            }
        }

    }
}
