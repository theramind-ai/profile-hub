# Multi-stage Dockerfile for Angular
FROM node:20-alpine as builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build:prod

# Runtime stage
FROM nginx:alpine

# Copy built application to nginx
COPY --from=builder /app/dist/profile-hub-frontend /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
