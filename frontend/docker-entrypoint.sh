#!/bin/sh
set -e

# Default backend URL if not provided
BACKEND_URL=${BACKEND_URL:-http://backend:8080}

echo "Configuring nginx with BACKEND_URL: $BACKEND_URL"

# Replace environment variables in nginx config
envsubst '${BACKEND_URL}' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

echo "Starting nginx..."
exec nginx -g 'daemon off;'
