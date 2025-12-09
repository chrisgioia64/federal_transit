#!/bin/bash
# Script to update the App Runner service to use a simple TCP health check.

# --- Configuration Variables (YOU MUST REPLACE THESE) ---
# NOTE: Replace YOUR_ACCOUNT_ID and SERVICE_ID with your actual V2 service details
# The SERVICE_ARN is critical for targeting the correct App Runner service.
SERVICE_ARN="arn:aws:apprunner:us-east-1:339713170431:service/federal-transit-api-v2/ef19e2a7f3144b2db21d6578fb30127e" 
AWS_REGION="us-east-1"
APP_PORT="5000" 

echo "Updating service ${SERVICE_ARN} to use TCP Health Check on port ${APP_PORT}."

# The command that updates the service configuration
aws apprunner update-service \
    --service-arn "${SERVICE_ARN}" \
    --health-check-configuration Protocol=TCP,Interval=10,Timeout=5,HealthyThreshold=1,UnhealthyThreshold=5 \
    --region "${AWS_REGION}"

if [ $? -eq 0 ]; then
    echo -e "\nUpdate successful. App Runner is deploying again using the TCP check."
    echo "Monitor the service status in the AWS Console. It should transition to 'Running' in the next few minutes."
else
    echo -e "\nERROR: Update failed. Check ARN format and AWS permissions."
fi