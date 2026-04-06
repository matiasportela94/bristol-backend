FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY bristol-api ./bristol-api
COPY bristol-application ./bristol-application
COPY bristol-domain ./bristol-domain
COPY bristol-infrastructure ./bristol-infrastructure
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=build ./bristol-api/target/bristol-api-3.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]