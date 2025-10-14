pipeline {
  agent any
 
  environment {
    AWS_REGION = 'us-east-1'
    ECR_REPO = '690509489991.dkr.ecr.us-east-1.amazonaws.com/project-ecr-repo'
    IMAGE_TAG = "${env.BUILD_ID}"
    S3_BUCKET = 'jenkins-project-s3'
  }
 
  stages {
    stage('Checkout SCM') {
  steps {
    checkout([$class: 'GitSCM',
      branches: [[name: '*/main']],
      userRemoteConfigs: [[
        url: 'https://github.com/amansingh977/todo-app-aws.git',
        credentialsId: 'git'
      ]]
    ])
  }
}
 
    stage('Build Application') {
      steps {
      //  sh 'chmod +x mvnw' 
        sh 'mvn clean package -DskipTests'
      }
    }
 
    stage('Build Docker Image & Push to ECR') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credenetials']]) {
          sh """
            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${ECR_REPO}
            docker build -t ${ECR_REPO}:${IMAGE_TAG} .
            docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
            docker push ${ECR_REPO}:${IMAGE_TAG}
            docker push ${ECR_REPO}:latest
          """
        }
      }
    }
 
    stage('Upload Artifact to S3') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credenetials']]) {
          sh """
            aws s3 cp target/todoapp-0.0.1-SNAPSHOT.jar s3://${S3_BUCKET}/artifacts/todo-spring-${IMAGE_TAG}.jar
          """
        }
      }
    }
 
    stage('Deploy to EKS') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credenetials']]) {
          sh '''
            aws eks update-kubeconfig --name todo-eks --region $AWS_REGION
            kubectl apply -f k8s/deployment.yaml
            kubectl apply -f k8s/service.yaml
          '''
        }
      }
    }
    stage('Smoke Test'){
      steps{
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credenetials']]) {
          sh '''
            ENDPOINT=$(kubectl get svc todo-spring-service -o jsonpath="{.status.loadBalancer.ingress[0].hostname}")  
            curl -I http://$ENDPOINT/
          '''
        }
      }
    }
  }
 
  post {
    success {
      echo '✅ Deployment completed successfully!'
    }
    failure {
      echo '❌ Deployment failed. Check logs for details.'
    }
  }
}
