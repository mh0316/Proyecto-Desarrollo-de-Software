# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# 1. Copiar primero los archivos esenciales
COPY .mvn/ .mvn
COPY mvnw ./
COPY pom.xml ./

# 2. Corregir permisos y formato del mvnw (CRÍTICO)
RUN chmod +x mvnw
# Instalar herramientas para corregir formato de archivos Windows
RUN apk add --no-cache dos2unix
RUN dos2unix mvnw
RUN chmod +x mvnw

# 3. Verificar que los archivos están presentes
RUN ls -la

# 4. Descargar dependencias (con timeout extendido)
RUN ./mvnw dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=3

# 5. Copiar código fuente
COPY src ./src

# 6. Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]