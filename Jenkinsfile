//jenkins > github-action 으로 CI/CD 재구현

pipeline {
    agent any
    environment {
        EC2_IP = credentials('leafmap-ec2') // Jenkins Credentials에서 호출
    }
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'develop', url: 'https://github.com/Heartravelers/leaf-map-BE.git'
            }
        }
        stage('Deploy to EC2') {
            steps {
                sshagent(['leafmapKey']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ec2-user@$EC2_IP << 'EOF'
                    cd /home/ec2-user/leafmap/leaf-map-BE
                    git pull origin develop
                    ./deploy.sh
                    EOF
                    """
                }
            }
        }
    }
}
