# Etapa única: solo runtime (mucho más pequeño y rápido)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root (seguridad)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar SOLO el JAR ya generado por ti en local
# Importante: el nombre debe coincidir exactamente con el que genera tu pom
COPY --chown=spring:spring target/*.jar app.jar

# Puerto
EXPOSE 8080

# Variables de entorno (puedes sobrescribirlas en docker-compose)
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"


# Arrancar la app
ENTRYPOINT sh -c "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"