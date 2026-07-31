FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/Spotisee-0.1.jar ./spotisee.jar
COPY src/main/resources/config.docker.yml ./config.yml

EXPOSE 8080
EXPOSE 8081

CMD ["java", "-jar", "spotisee.jar", "server", "config.yml"]