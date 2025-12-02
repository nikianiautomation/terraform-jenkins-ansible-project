provider "aws" {
    region = "us-east-1" 
    }
resource "aws_instance" "ec2_ansible" {
    ami = "ami-0fa3fe0fa7920f68e"
    instance_type = "t2.xlarge"
    count = 3
    key_name = "terraform_keypair"
    vpc_security_group_ids = ["sg-09aff6868bcd29acf"]
    tags = {
       Name = "ec2_ansible"

    }
}