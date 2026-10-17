FROM node:26-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build 
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Nginx läuft standardmäßig auf Port 80
EXPOSE 80
# Nginx im Vordergrund starten
CMD ["nginx", "-g", "daemon off;"]
