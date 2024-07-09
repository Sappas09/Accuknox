#!/bin/bash

# Start Minikube (if not already started)
minikube start

# Apply the frontend deployment
kubectl apply -f frontend-deployment.yaml

# Wait for deployment to be ready (optional)
sleep 10

# Get the frontend service URL
FRONTEND_URL=$(minikube service frontend-service --url)

# Update stg_config.properties with the frontend URL
sed -i "s#^frontend.url=.*#frontend.url=${FRONTEND_URL}#" AccuKnox/src/test/resources/stg_config.properties

# Clean up: Delete the frontend deployment
kubectl delete -f frontend-deployment.yaml
