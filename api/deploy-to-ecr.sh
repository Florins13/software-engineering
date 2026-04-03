#!/bin/bash

# AWS ECR Deployment Script for Quarkus Application
# Usage: ./deploy-to-ecr.sh [AWS_REGION] [ECR_REPOSITORY_NAME] [IMAGE_TAG]

set -e

# Configuration
AWS_REGION="${1:-eu-west-3}"
ECR_REPO_NAME="${2:-bike-shop-dev}"
IMAGE_TAG="${3:-latest}"

echo "🚀 Starting deployment to AWS ECR..."
echo "   Region: $AWS_REGION"
echo "   Repository: $ECR_REPO_NAME"
echo "   Tag: $IMAGE_TAG"
echo ""

# Get AWS account ID
echo "📋 Getting AWS account ID..."
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "   Account ID: $AWS_ACCOUNT_ID"
echo ""

# Construct ECR repository URI
ECR_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
FULL_IMAGE_NAME="${ECR_URI}/${ECR_REPO_NAME}:${IMAGE_TAG}"

# Login to ECR
echo "🔐 Logging in to Amazon ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_URI
echo ""

# Build the Docker image
echo "🔨 Building Docker image..."
docker build -t $ECR_REPO_NAME:$IMAGE_TAG -t $FULL_IMAGE_NAME .
echo "   ✅ Image built successfully"
echo ""

# Push to ECR
echo "📤 Pushing image to ECR..."
docker push $FULL_IMAGE_NAME
echo "   ✅ Image pushed successfully"
echo ""

# Tag and push 'latest' if not already
if [ "$IMAGE_TAG" != "latest" ]; then
    echo "🏷️  Tagging and pushing as 'latest'..."
    docker tag $FULL_IMAGE_NAME "${ECR_URI}/${ECR_REPO_NAME}:latest"
    docker push "${ECR_URI}/${ECR_REPO_NAME}:latest"
    echo "   ✅ Latest tag pushed"
    echo ""
fi

# Get image details
echo "📊 Image Details:"
aws ecr describe-images \
    --repository-name $ECR_REPO_NAME \
    --region $AWS_REGION \
    --image-ids imageTag=$IMAGE_TAG \
    --query 'imageDetails[0].[imagePushedAt,imageSizeInBytes,imageDigest]' \
    --output table

echo ""
echo "✨ Deployment complete!"
echo ""
echo "🎯 Image URI: $FULL_IMAGE_NAME"
echo ""
echo "📝 To pull this image:"
echo "   docker pull $FULL_IMAGE_NAME"
echo ""
echo "🚀 To run locally:"
echo "   docker run -p 8080:8080 $FULL_IMAGE_NAME"
