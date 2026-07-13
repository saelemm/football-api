# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn
COPY football-domain ./football-domain
COPY football-application ./football-application
COPY football-adapter-persistence-jpa ./football-adapter-persistence-jpa
COPY football-adapter-rest ./football-adapter-rest
COPY football-bootstrap ./football-bootstrap

RUN chmod +x mvnw && ./mvnw -pl football-bootstrap -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/football-bootstrap/target/football-bootstrap-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

