#!/bin/bash

#jenkins installation 
sudo yum update –y
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
sudo yum upgrade
sudo yum install java-21-amazon-corretto -y
sudo yum install git -y
sudo yum install jenkins -y
sudo systemctl enable jenkins
sudo systemctl start jenkins

#dockerinstallation 
sudo yum update -y
sudo yum install git -y
sudo yum install -y docker
sudo chmod 666 /var/run/docker.sock
sudo systemctl start docker
sudo systemctl enable docker

#maven installation 
sudo wget -O /tmp/apache-maven-3.9.9-bin.tar.gz https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz

sudo tar -xzf /tmp/apache-maven-3.9.9-bin.tar.gz -C /opt

echo "export PATH=/opt/apache-maven-3.9.9/bin:$PATH" >> ~/.bashrc

source ~/.bashrc
