# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder AS app-stage
COPY . /
RUN mvn clean install -DskipTests

#
# RUN THE APPLICATION
#
FROM openjdk:18-ea-bullseye
RUN apt-get update & apt-get upgrade

COPY --from=app-stage target/spring-boot-backend.jar ./

RUN useradd -u 2000 spring-boot-backed
USER spring-boot-backed

CMD ["java", "-jar", "spring-boot-backend.jar"]