FROM openjdk:21-jdk
LABEL maintainer="deepdive"

ARG JAR_FILE=build/libs/*.jar

ENV CUSTOM_NAME default

COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]