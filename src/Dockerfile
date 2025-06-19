# Etapa 1: construir el jar
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos los archivos de configuración y código
COPY pom.xml .
COPY src ./src

# Construimos el jar (sin tests para acelerar)
RUN mvn clean package -DskipTests

# Etapa 2: crear la imagen ligera para ejecutar la app
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiamos el jar desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]