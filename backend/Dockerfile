# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -B


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/Spotisee-0.1.jar ./spotisee.jar
COPY --from=build /app/src/main/resources/config.docker.yml ./config.yml

EXPOSE 8080
EXPOSE 8081

CMD ["java", "-jar", "spotisee.jar", "server", "config.yml"]