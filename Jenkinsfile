// Pipeline declarativo de estudo: build Maven multi-modulo, testes, build das
// imagens Docker, import das imagens no containerd do k3s (sem registry externo)
// e deploy no Kubernetes via kubectl apply.
pipeline {
    agent any

    environment {
        // Nomes/tags das imagens locais. Como nao usamos registry externo, ficam
        // sempre como ":latest" e o deploy usa imagePullPolicy: Never nos manifests
        // (ver k8s/manifests/service-a|service-b/deployment.yaml).
        SERVICE_A_IMAGE = "service-a:latest"
        SERVICE_B_IMAGE = "service-b:latest"
    }

    stages {

        stage('Checkout') {
            // Traz o codigo-fonte do repositorio configurado no job do Jenkins.
            // E redundante se o job ja faz um "checkout scm" implicito antes do
            // Jenkinsfile rodar, mas deixamos explicito aqui por clareza didatica.
            steps {
                checkout scm
            }
        }

        stage('Build (mvn clean package)') {
            // Compila e empacota os dois modulos (service-a e service-b) a partir
            // do pom.xml pai (reactor multi-modulo). Os testes sao pulados aqui de
            // proposito: eles rodam em uma stage propria logo a seguir, para o
            // pipeline ficar mais claro na tela de stages do Jenkins (fica visivel
            // separadamente se o problema foi de compilacao ou de teste).
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            // Roda a suite de testes de todos os modulos do reactor Maven.
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    // Publica os relatorios JUnit no Jenkins (aba "Test Result").
                    // allowEmptyResults evita falha caso ainda nao existam classes
                    // de teste em algum modulo (comum numa fase inicial de estudo).
                    junit testResults: 'service-a/target/surefire-reports/*.xml,service-b/target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('Docker Build') {
            // Builda a imagem de cada microsservico usando os Dockerfiles
            // multi-stage (maven:3.9-eclipse-temurin-17 -> eclipse-temurin:17-jre-alpine).
            // O contexto de build precisa ser a RAIZ do repositorio, pois e um
            // projeto Maven multi-modulo: o Dockerfile de cada servico copia o
            // pom.xml pai e o pom.xml do outro modulo para o Maven conseguir
            // resolver o reactor durante o build (ver comentarios nos Dockerfiles).
            steps {
                sh "docker build -f service-a/Dockerfile -t ${SERVICE_A_IMAGE} ."
                sh "docker build -f service-b/Dockerfile -t ${SERVICE_B_IMAGE} ."
            }
        }

        stage('Import images to k3s containerd') {
    // O Jenkins agora roda numa instancia separada do k3s, entao a
    // importacao das imagens precisa ser feita remotamente via SSH.
    // O usuario 'jenkins' ja tem uma chave autorizada na instancia do k3s
    // (ver Modulo 12c) e sudo sem senha do lado de la.
    environment {
        K3S_HOST = "ubuntu@172.31.46.242"
    }
    steps {
        sh "docker save ${SERVICE_A_IMAGE} | ssh -o StrictHostKeyChecking=accept-new ${K3S_HOST} 'sudo k3s ctr images import -'"
        sh "docker save ${SERVICE_B_IMAGE} | ssh -o StrictHostKeyChecking=accept-new ${K3S_HOST} 'sudo k3s ctr images import -'"
    }
}

        stage('Deploy to Kubernetes') {
            // Aplica todos os manifests do repositorio: Deployments, Services e
            // ConfigMaps de service-a, service-b, Prometheus e Grafana. O flag
            // -R (recursive) e necessario porque os manifests estao organizados
            // em uma subpasta por componente dentro de k8s/manifests/.
            steps {
                sh 'kubectl apply -f k8s/manifests/ -R'

                // Como a tag da imagem continua sempre ":latest", o "kubectl
                // apply" sozinho nao percebe que ha uma imagem nova (o texto do
                // Deployment nao mudou). Por isso forcamos um rollout restart dos
                // nossos dois servicos, para os pods serem recriados e usarem a
                // imagem que acabou de ser importada no k3s.
                sh 'kubectl rollout restart deployment/service-a deployment/service-b'
                sh 'kubectl rollout status deployment/service-a --timeout=120s'
                sh 'kubectl rollout status deployment/service-b --timeout=120s'
            }
        }
    }

    post {
        success {
            echo 'Pipeline concluido: imagens buildadas, importadas no k3s e manifests aplicados com sucesso.'
        }
        failure {
            echo 'Pipeline falhou - verifique os logs da stage que quebrou acima.'
        }
    }
}
