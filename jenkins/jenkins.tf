provider "aws" {
    region = "us-east-1" 
    }
resource "aws_instance" "ec2_jenkins" {
    ami = "ami-0fa3fe0fa7920f68e"
    instance_type = "t2.xlarge"
    key_name = "02-12-2025"
    vpc_security_group_ids = ["sg-0a20c5d8366a1d54f"]
    tags = {
       Name = "ec2_jenkins"
    }
    user_data = file("resources/docker_jenkins_maven_installation.sh")
}