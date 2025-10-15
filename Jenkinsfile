 pipeline {
  agent any

  environment {
    AWS_REGION = 'us-east-1'
    ECR_REPO = '930832106289.dkr.ecr.us-east-1.amazonaws.com/project-ecr-repo'
    IMAGE_TAG = "${env.BUILD_ID}"
    S3_BUCKET = 'todo-app-project-bucket'
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
        dir('todo-springboot') {
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Build Docker Image & Push to ECR') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh """
            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${ECR_REPO}
            docker build -t ${ECR_REPO}:${IMAGE_TAG} todo-springboot
            docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
            docker push ${ECR_REPO}:${IMAGE_TAG}
            docker push ${ECR_REPO}:latest
          """
        }
      }
    }

    stage('Upload Artifact to S3') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh """
            aws s3 cp todo-springboot/target/todoapp-0.0.1-SNAPSHOT.jar s3://${S3_BUCKET}/artifacts/todo-spring-${IMAGE_TAG}.jar
          """
        }
      }
    }

    stage('Deploy to EKS') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh """
            aws eks update-kubeconfig --name todo-app-project --region $AWS_REGION
            kubectl apply -f todo-springboot/k8s/deployment.yaml
            kubectl apply -f todo-springboot/k8s/service.yaml
          """
        }
      }
    }

    stage('Smoke Test') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh '''
            echo "⏳ Waiting for LoadBalancer hostname..."
            for i in {1..10}; do
              ENDPOINT=$(kubectl get svc todo-spring-service -o jsonpath="{.status.loadBalancer.ingress[0].hostname}")
              if [ -n "$ENDPOINT" ]; then
                echo "✅ Found endpoint: $ENDPOINT"
                break
              fi
              echo "Waiting for ELB to be provisioned..."
              sleep 10
            done

            if [ -z "$ENDPOINT" ]; then
              echo "❌ LoadBalancer hostname not available after timeout."
              exit 1
            fi

            echo "🌐 Testing endpoint: http://$ENDPOINT/"
            for i in {1..5}; do
              STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://$ENDPOINT/)
              if [ "$STATUS" = "200" ]; then
                echo "✅ Smoke test passed."
                exit 0
              fi
              echo "Waiting for service to become healthy (HTTP $STATUS)..."
              sleep 5
            done

            echo "❌ Smoke test failed after retries."
            exit 1
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
