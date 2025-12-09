#!/bin/bash

# Set these variables
AWS_REGION="us-east-1"
AWS_ACCOUNT_ID="339713170431"  # Replace with your account ID
REPOSITORY_NAME="federal-transit"

# Step 1: Create ECR repository (if it doesn't exist)
echo "Creating ECR repository..."
aws ecr create-repository --repository-name $REPOSITORY_NAME --region $AWS_REGION 2>/dev/null || echo "Repository already exists"

# Step 2: Get ECR login
echo "Logging into ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Step 3: Build the image
echo "Building Docker image..."
docker build -t $REPOSITORY_NAME .

# Step 4: Tag the image
echo "Tagging image for ECR..."
docker tag $REPOSITORY_NAME:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPOSITORY_NAME:latest

# Step 5: Push the image
echo "Pushing image to ECR..."
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPOSITORY_NAME:latest

echo "Done! Image URI: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPOSITORY_NAME:latest"