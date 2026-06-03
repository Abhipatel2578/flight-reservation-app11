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
                 mvn clean verify sonar:sonar \
                 -Dsonar.projectKey=Flight \
                 -Dsonar.host.url=http://15.237.197.202:9000 \
                 -Dsonar.login=sqp_9d44103f78fc1e6202fb5e3ede1efbedfb5ba8ca'''
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
         // stage('Update-Deployment'){
           //     steps{
             //       sh"""
               //     IMAGE_TAG=${BUILD_NUMBER}
                 //   sed -i 's|image: abhi2578/flightreservation-new:.*|image: abhi2578/flightreservation-new:${IMAGE_TAG}|g' FlightReservationApplication/k8s/deployment.yaml
                   //     """
               // }
         // } 
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

    
