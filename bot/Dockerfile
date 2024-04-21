FROM eclipse-temurin:21-alpine
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bot.jar

EXPOSE 8090
ENTRYPOINT java -jar bot.jar
