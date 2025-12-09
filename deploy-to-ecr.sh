#!/bin/bash

# ECR Deployment Script for Federal Transit Application
# This script builds and pushes your Docker image to Amazon ECR

set -euo pipefail  # Exit on error, undefined vars, and pipe failures

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
REPOSITORY_NAME="federal-transit"
IMAGE_TAG="latest"

# Function to print error and exit
error_exit() {
    echo -e "${RED}Error: $1${NC}" >&2
    exit 1
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

echo -e "${GREEN}=== ECR Deployment Script ===${NC}\n"

# Prerequisites check
echo -e "${BLUE}Checking prerequisites...${NC}"
if ! command_exists aws; then
    error_exit "AWS CLI is not installed. Please install it first."
fi

if ! command_exists docker; then
    error_exit "Docker is not installed. Please install Docker Desktop first."
fi

# Check if Docker daemon is running
if ! docker info >/dev/null 2>&1; then
    error_exit "Docker daemon is not running. Please start Docker Desktop."
fi

echo -e "${GREEN}✓ Prerequisites check passed${NC}\n"

# Step 1: Get AWS Account ID
echo -e "${YELLOW}Step 1: Getting AWS Account ID...${NC}"
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text 2>/dev/null || true)

if [ -z "$AWS_ACCOUNT_ID" ] || [ "$AWS_ACCOUNT_ID" = "None" ]; then
    error_exit "Could not get AWS Account ID. Make sure AWS CLI is configured.\nRun: aws configure"
fi

echo -e "${GREEN}✓ AWS Account ID: $AWS_ACCOUNT_ID${NC}\n"

# Step 2: Get AWS Region
echo -e "${YELLOW}Step 2: Getting AWS Region...${NC}"
AWS_REGION=$(aws configure get region 2>/dev/null || echo "")

if [ -z "$AWS_REGION" ]; then
    echo -e "${YELLOW}Warning: No default region configured. Using us-east-1 as default.${NC}"
    AWS_REGION="us-east-1"
fi

echo -e "${GREEN}✓ AWS Region: $AWS_REGION${NC}\n"

# Construct ECR repository URI
ECR_REPOSITORY_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPOSITORY_NAME"
ECR_IMAGE_URI="$ECR_REPOSITORY_URI:$IMAGE_TAG"

echo -e "${GREEN}ECR Repository URI: $ECR_REPOSITORY_URI${NC}"
echo -e "${GREEN}ECR Image URI: $ECR_IMAGE_URI${NC}\n"

# Step 3: Create ECR repository (if it doesn't exist)
echo -e "${YELLOW}Step 3: Creating ECR repository (if it doesn't exist)...${NC}"
if aws ecr describe-repositories --repository-names "$REPOSITORY_NAME" --region "$AWS_REGION" &>/dev/null; then
    echo -e "${GREEN}✓ Repository '$REPOSITORY_NAME' already exists${NC}\n"
else
    echo -e "${YELLOW}Creating new repository...${NC}"
    if aws ecr create-repository --repository-name "$REPOSITORY_NAME" --region "$AWS_REGION" &>/dev/null; then
        echo -e "${GREEN}✓ Repository '$REPOSITORY_NAME' created successfully${NC}\n"
    else
        error_exit "Failed to create ECR repository. Check your AWS permissions."
    fi
fi

# Step 4: Login to ECR
echo -e "${YELLOW}Step 4: Logging into Amazon ECR...${NC}"
if aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REPOSITORY_URI" 2>/dev/null; then
    echo -e "${GREEN}✓ Successfully logged into ECR${NC}\n"
else
    error_exit "Failed to login to ECR. Check your AWS credentials and Docker setup."
fi

# Step 5: Build Docker image
echo -e "${YELLOW}Step 5: Building Docker image...${NC}"
echo -e "${YELLOW}This may take several minutes...${NC}"
if [ ! -f "Dockerfile" ]; then
    error_exit "Dockerfile not found in current directory."
fi

if docker build -t "${REPOSITORY_NAME}:${IMAGE_TAG}" . ; then
    echo -e "${GREEN}✓ Docker image built successfully${NC}\n"
else
    error_exit "Docker build failed. Check the Dockerfile and build logs above."
fi

# Step 6: Tag the image for ECR
echo -e "${YELLOW}Step 6: Tagging image for ECR...${NC}"
if docker tag "${REPOSITORY_NAME}:${IMAGE_TAG}" "$ECR_IMAGE_URI"; then
    echo -e "${GREEN}✓ Image tagged successfully${NC}\n"
else
    error_exit "Failed to tag image. Make sure the build completed successfully."
fi

# Step 7: Push image to ECR
echo -e "${YELLOW}Step 7: Pushing image to ECR...${NC}"
echo -e "${YELLOW}This may take several minutes...${NC}"
if docker push "$ECR_IMAGE_URI"; then
    echo -e "${GREEN}✓ Image pushed successfully${NC}\n"
else
    error_exit "Failed to push image. Check your ECR permissions and network connection."
fi

# Success message
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}\n"
echo -e "Image URI: ${GREEN}$ECR_IMAGE_URI${NC}\n"
echo -e "${BLUE}Next steps:${NC}"
echo -e "1. Go to AWS App Runner Console"
echo -e "   https://console.aws.amazon.com/apprunner/"
echo -e "2. Click 'Create an App Runner service'"
echo -e "3. Select 'Container registry' → 'Amazon ECR'"
echo -e "4. Choose repository: ${GREEN}$REPOSITORY_NAME${NC}"
echo -e "5. Select image tag: ${GREEN}$IMAGE_TAG${NC}"
echo -e "6. Configure service settings:"
echo -e "   - Port: ${GREEN}5000${NC}"
echo -e "   - CPU/Memory: As needed"
echo -e "7. Create the service\n"
echo -e "${BLUE}To update your application:${NC}"
echo -e "Run this script again after making code changes.\n"

