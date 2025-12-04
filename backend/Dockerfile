# Etapa de build: compila el proyecto con Maven y JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia el POM y descarga dependencias (mejor caché)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copia el código y compila
COPY src ./src
RUN mvn -B -DskipTests package && cp target/*.jar app.jar

# Etapa de runtime: solo JRE (más pequeño)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring

# Config
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# Copia el artefacto construido en la etapa anterior
COPY --chown=spring:spring --from=build /app/app.jar app.jar

# Cambiar a usuario spring
USER spring:spring

# Arranque
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]