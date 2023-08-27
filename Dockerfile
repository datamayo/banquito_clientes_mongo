FROM eclipse-temurin:17-jre-focal
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} client.jar
ENTRYPOINT ["java","-jar","/client.jar"]