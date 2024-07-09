# Setting up Minikube and Docker for Project Deployment

## Install Minikube

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube && rm minikube-linux-amd64
```

## Install Docker

```bash
sudo apt update
sudo apt install -y docker.io
sudo systemctl enable docker --now
```

## Configure Docker Permissions

```bash
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker
```

Verify Docker installation:

```bash
docker run hello-world
```

## Fix Docker Permissions (if necessary)

```bash
sudo chown "$USER":"$USER" /home/"$USER"/.docker -R
sudo chmod g+rwx "$HOME/.docker" -R
```

## Start Minikube

```bash
minikube start
```

## Deploy Project using Kubernetes

```bash
cd /qa-test/Deployment
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml
kubectl get deployments
```

## Access Frontend Service

```bash
minikube service frontend-service --url 


```
[ Above comment with give you the FrontEnd URL Ex: http://192.168.49.2:30292 ]

## Import and Update Maven Project

- Import the Maven project from [Git Link].
- Update the Maven project:
- Right-click on project -> Maven -> Update Project.

## Configure TestNG and Maven

Ensure TestNG and Maven are installed on your machine.

## Configure Project Settings

Navigate to `stg_config.properties` file and replace `frontend URL` with `baseURL`.
-> [ Ex: http://192.168.49.2:30292 ]

Save the property file.

## Run Test Cases
 Navigate to the project folder -> [ AccuKnox ] & Execute the below mvn command 
```bash
cd /home/appas/Appas/My Projects/Selenium Hybrid Framework/AccuKnox
mvn test
```

## View Test Reports

```bash
cd ExtendReport
# Open the HTML report in any browser to view test case details.
```

## Review Test Cases

Navigate to test case implementation:

```bash
cd /AccuKnox/src/test/java/org/selenium/tests/HomePageTest.java
# Explore connected class files for more details.
```


Refer This video for more clarification : [ https://drive.google.com/file/d/1AxbE3ovLsAaXVXD9DnDLFofc-6Lv3AqA/view?usp=sharing ] 