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
        dir('todo-springboot') {
          sh 'mvn clean package -DskipTests'
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        sh """
          docker build -t ${ECR_REPO}:${IMAGE_TAG} todo-springboot
          docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
        """
      }
    }

    stage('Push Docker Image to ECR') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh """
            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${ECR_REPO}
            docker push ${ECR_REPO}:${IMAGE_TAG}
            docker push ${ECR_REPO}:latest
          """
        }
      }
    }

    stage('Deploy to EKS') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh """
            aws eks update-kubeconfig --name project-eks-cluster --region $AWS_REGION
            kubectl apply -f todo-springboot/k8s/deployment.yaml
            kubectl apply -f todo-springboot/k8s/service.yaml
          """
        }
      }
    }
  }

  post {
    success {
      echo '✅ Deployment completed successfully!'
      withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
        sh """
          aws s3 cp todo-springboot/target/todoapp-0.0.1-SNAPSHOT.jar s3://${S3_BUCKET}/artifacts/todo-spring-${IMAGE_TAG}.jar
        """
      }
    }
    failure {
      echo '❌ Deployment failed. Check logs for details.'
    }
  }
}













// pipeline {
//   agent any

//   environment {
//     AWS_REGION = 'us-east-1'
//     ECR_REPO = '690509489991.dkr.ecr.us-east-1.amazonaws.com/project-ecr-repo'
//     IMAGE_TAG = "${env.BUILD_ID}"
//     S3_BUCKET = 'jenkins-project-s3'
//     SERVICE_URL = 'http://a1b2c3d4e5f6g7h8.us-east-1.elb.amazonaws.com/actuator/health' // Replace with actual LoadBalancer URL
//   }

//   stages {
//     stage('Checkout SCM') {
//       steps {
//         checkout([$class: 'GitSCM',
//           branches: [[name: '*/main']],
//           userRemoteConfigs: [[
//             url: 'https://github.com/amansingh977/todo-app-aws.git',
//             credentialsId: 'git'
//           ]]
//         ])
//       }
//     }

//     stage('Build Application') {
//       steps {
//         dir('todo-springboot') {
//           sh 'mvn clean package -DskipTests'
//         }
//       }
//     }

//     stage('Build Docker Image') {
//       steps {
//         sh """
//           docker build -t ${ECR_REPO}:${IMAGE_TAG} todo-springboot
//           docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
//         """
//       }
//     }

//     stage('Push Docker Image to ECR') {
//       steps {
//         withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
//           sh """
//             aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${ECR_REPO}
//             docker push ${ECR_REPO}:${IMAGE_TAG}
//             docker push ${ECR_REPO}:latest
//           """
//         }
//       }
//     }

//     stage('Deploy to EKS') {
//       steps {
//         withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
//           sh """
//             aws eks update-kubeconfig --name project-eks-cluster --region $AWS_REGION
//             kubectl apply -f todo-springboot/k8s/deployment.yaml
//             kubectl apply -f todo-springboot/k8s/service.yaml
//           """
//         }
//       }
//     }

//     stage('Smoke Test') {
//       steps {
//         script {
//           echo "Running smoke test against ${SERVICE_URL}..."
//           def response = sh(script: "curl -s -o /dev/null -w \"%{http_code}\" ${SERVICE_URL}", returnStdout: true).trim()
//           if (response != '200') {
//             error "❌ Smoke test failed! Expected HTTP 200 but got ${response}"
//           } else {
//             echo "✅ Smoke test passed. Service is healthy."
//           }
//         }
//       }
//     }
//   }

//   post {
//     success {
//       echo '✅ Deployment completed successfully!'
//       withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
//         sh """
//           aws s3 cp todo-springboot/target/todoapp-0.0.1-SNAPSHOT.jar s3://${S3_BUCKET}/artifacts/todo-spring-${IMAGE_TAG}.jar
//         """
//       }
//     }
//     failure {
//       echo '❌ Deployment failed. Check logs for details.'
//     }
//   }
// }
