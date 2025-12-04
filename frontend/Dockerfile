# Etapa 1: Build con Node 20
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci --legacy-peer-deps

COPY . .
RUN npm run build

# Etapa 2: Nginx
FROM nginx:alpine

# Copiar los archivos compilados
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html/

# Copiar configuración de nginx para SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
