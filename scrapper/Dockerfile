FROM eclipse-temurin:21-alpine
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} scrapper.jar

EXPOSE 8080
ENTRYPOINT java -jar scrapper.jar
