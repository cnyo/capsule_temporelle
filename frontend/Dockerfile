# Multi-stage build for Angular frontend
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build the application
# Use production configuration by default, can be overridden with build args
ARG CONFIGURATION=production
RUN npm run build -- --configuration=${CONFIGURATION}

# Runtime stage with Nginx
FROM nginx:alpine

# Copy custom nginx config template
COPY nginx.conf.template /etc/nginx/nginx.conf.template

# Copy entrypoint script
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Copy built application from builder stage
COPY --from=builder /app/dist/TimeCapsule/browser /usr/share/nginx/html

# Expose port
EXPOSE 9004

# Environment variable for backend URL
ENV BACKEND_URL=http://backend:9004

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:9004 || exit 1

# Run entrypoint script
ENTRYPOINT ["/docker-entrypoint.sh"]
