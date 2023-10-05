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

COPY --from=app-stage backend/target/classes/static/ ./src/main/resources/static/
COPY --from=app-stage backend/target/vsds-demonstrator.jar ./

RUN useradd -u 2000 vsds-demonstrator
USER vsds-demonstrator

CMD ["java", "-jar", "vsds-demonstrator.jar"]