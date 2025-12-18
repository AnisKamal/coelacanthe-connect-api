FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests



FROM openjdk:21-ea-1-slim
COPY --from=build /target/coelacanthe-api-0.0.1-SNAPSHOT.jar coelacanthe-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","coelacanthe-api.jar"]

