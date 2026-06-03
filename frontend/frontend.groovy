pipeline{
    agent any
    stages{
        stage('code-pull'){
            steps{
                git branch: 'main', url: 'https://github.com/Abhipatel2578/flight-reservation-app11.git'
            }
        }
        stage('build'){
            steps{
                sh '''
                    cd frontend
                    npm install
                    npm run build
                '''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                    cd frontend
                    aws s3 sync dist/ s3://flight-2578/ --acl public-read
                '''
            }
        }
    }
}
