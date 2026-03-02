# syntax=docker/dockerfile:1.7

FROM maven:4.0.0-rc-5-sapmachine-21 AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests clean package \
    && JAR_FILE="$(ls target/*.jar | grep -v '\.original$' | head -n 1)" \
    && cp "${JAR_FILE}" target/app.jar

FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

COPY --from=build /app/target/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
