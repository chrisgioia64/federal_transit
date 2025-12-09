
ECR_ACCESS_ROLE_ARN="arn:aws:iam:339713170431:role/service-role/AppRunnerECRAccessRole"

docker build -t federal-transit .

docker tag federal-transit:latest 339713170431.dkr.ecr.us-east-1.amazonaws.com/federal-transit:latest

docker push 339713170431.dkr.ecr.us-east-1.amazonaws.com/federal-transit:latest

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 339713170431.dkr.ecr.us-east-1.amazonaws.com

aws apprunner create-service \
    --cli-input-json file://apprunner.json \
    --region us-east-1
