# ECR Deployment Script for Federal Transit Application
# This script builds and pushes your Docker image to Amazon ECR

$ErrorActionPreference = "Stop"  # Exit on any error

# Configuration
$REPOSITORY_NAME = "federal-transit"
$IMAGE_TAG = "latest"

Write-Host "=== ECR Deployment Script ===" -ForegroundColor Green
Write-Host ""

# Step 1: Get AWS Account ID
Write-Host "Step 1: Getting AWS Account ID..." -ForegroundColor Yellow
try {
    $AWS_ACCOUNT_ID = (aws sts get-caller-identity --query Account --output text 2>$null)
    
    if ([string]::IsNullOrWhiteSpace($AWS_ACCOUNT_ID)) {
        Write-Host "Error: Could not get AWS Account ID. Make sure AWS CLI is configured." -ForegroundColor Red
        Write-Host "Run: aws configure" -ForegroundColor Yellow
        exit 1
    }
    
    Write-Host "✓ AWS Account ID: $AWS_ACCOUNT_ID" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Error: Failed to get AWS Account ID. Make sure AWS CLI is installed and configured." -ForegroundColor Red
    Write-Host "Run: aws configure" -ForegroundColor Yellow
    exit 1
}

# Step 2: Get AWS Region
Write-Host "Step 2: Getting AWS Region..." -ForegroundColor Yellow
try {
    $AWS_REGION = (aws configure get region 2>$null)
    
    if ([string]::IsNullOrWhiteSpace($AWS_REGION)) {
        Write-Host "Warning: No default region configured. Using us-east-1 as default." -ForegroundColor Yellow
        $AWS_REGION = "us-east-1"
    }
    
    Write-Host "✓ AWS Region: $AWS_REGION" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Warning: Could not get region. Using us-east-1 as default." -ForegroundColor Yellow
    $AWS_REGION = "us-east-1"
    Write-Host ""
}

# Construct ECR repository URI
$ECR_REPOSITORY_URI = "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPOSITORY_NAME"
$ECR_IMAGE_URI = "$ECR_REPOSITORY_URI`:$IMAGE_TAG"

Write-Host "ECR Repository URI: $ECR_REPOSITORY_URI" -ForegroundColor Green
Write-Host "ECR Image URI: $ECR_IMAGE_URI" -ForegroundColor Green
Write-Host ""

# Step 3: Create ECR repository (if it doesn't exist)
Write-Host "Step 3: Creating ECR repository (if it doesn't exist)..." -ForegroundColor Yellow
try {
    $repoCheck = aws ecr describe-repositories --repository-names $REPOSITORY_NAME --region $AWS_REGION 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Repository '$REPOSITORY_NAME' already exists" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "Creating new repository..." -ForegroundColor Yellow
        aws ecr create-repository --repository-name $REPOSITORY_NAME --region $AWS_REGION
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Repository '$REPOSITORY_NAME' created successfully" -ForegroundColor Green
            Write-Host ""
        } else {
            Write-Host "Error: Failed to create repository" -ForegroundColor Red
            exit 1
        }
    }
}
catch {
    Write-Host "Error: Failed to check/create repository" -ForegroundColor Red
    exit 1
}

# Step 4: Login to ECR
Write-Host "Step 4: Logging into Amazon ECR..." -ForegroundColor Yellow
try {
    $loginPassword = aws ecr get-login-password --region $AWS_REGION
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Failed to get ECR login password" -ForegroundColor Red
        exit 1
    }
    
    $loginPassword | docker login --username AWS --password-stdin $ECR_REPOSITORY_URI
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Successfully logged into ECR" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "Error: Failed to login to ECR" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error: Failed to login to ECR" -ForegroundColor Red
    Write-Host "Make sure Docker is running and AWS credentials are valid." -ForegroundColor Yellow
    exit 1
}

# Step 5: Build Docker image
Write-Host "Step 5: Building Docker image..." -ForegroundColor Yellow
Write-Host "This may take several minutes..." -ForegroundColor Yellow
try {
    docker build -t "${REPOSITORY_NAME}:${IMAGE_TAG}" .
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Docker image built successfully" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "Error: Docker build failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error: Docker build failed" -ForegroundColor Red
    Write-Host "Make sure Docker is running and the Dockerfile is present." -ForegroundColor Yellow
    exit 1
}

# Step 6: Tag the image for ECR
Write-Host "Step 6: Tagging image for ECR..." -ForegroundColor Yellow
try {
    docker tag "${REPOSITORY_NAME}:${IMAGE_TAG}" $ECR_IMAGE_URI
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Image tagged successfully" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "Error: Failed to tag image" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error: Failed to tag image" -ForegroundColor Red
    exit 1
}

# Step 7: Push image to ECR
Write-Host "Step 7: Pushing image to ECR..." -ForegroundColor Yellow
Write-Host "This may take several minutes..." -ForegroundColor Yellow
try {
    docker push $ECR_IMAGE_URI
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Image pushed successfully" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "Error: Failed to push image" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error: Failed to push image" -ForegroundColor Red
    exit 1
}

# Success message
Write-Host "========================================" -ForegroundColor Green
Write-Host "✓ Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Image URI: $ECR_IMAGE_URI" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Go to AWS App Runner Console"
Write-Host "2. Create a new service"
Write-Host "3. Select 'Container registry' → 'Amazon ECR'"
Write-Host "4. Choose repository: $REPOSITORY_NAME" -ForegroundColor Green
Write-Host "5. Select image tag: $IMAGE_TAG" -ForegroundColor Green
Write-Host "6. Set port to: 5000" -ForegroundColor Green
Write-Host "7. Create the service"
Write-Host ""

