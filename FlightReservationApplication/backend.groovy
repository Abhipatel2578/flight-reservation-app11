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
              -Dsonar.host.url=http://13.38.30.9:9000 \
              -Dsonar.token=sqp_12a77664c430c104e957550b6a1ac4bcc1ab0d1f'''
            }
        }
                stage('Docker-build'){
            steps{
                withDockerRegistry(credentialsId: 'docker', url: 'https://index.docker.io/v1/')
                {
                sh '''
                    cd FlightReservationApplication
                    docker build -t abhi2578/flightreservation-new:${BUILD_NUMBER} .
                    docker push abhi2578/flightreservation-new:${BUILD_NUMBER}
                    docker rmi abhi2578/flightreservation-new:${BUILD_NUMBER}
                '''
               }
            }
        }
          stage('Update-Deployment'){
                steps{
                    sh'''
                    sed -i 's|image: abhi2578/flightreservation-new:.*|image: abhi2578/flightreservation-new:${BUILD_NUMBER}|g' FlightReservationApplication/k8s/deployment.yaml
                        '''
                }
          } 
         stage('Deploy'){
             steps{
                 sh'''
                 cd FlightReservationApplication
                 kubectl apply -f k8s/ '''
             }
         }
         }
         post {
    success {
        echo 'Pipeline completed successfully!'

        build job: 'flight-frontend'
    }

    failure {
        echo 'Pipeline failed!'
    }
}
        }

    
