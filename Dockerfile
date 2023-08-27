FROM eclipse-temurin:17-jre-focal
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} test.jar
ENTRYPOINT ["java","-jar","/client.jar"]