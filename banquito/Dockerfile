FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} banquito-0.0.1.jar
ENTRYPOINT ["java","-jar","/banquito-0.0.1.jar"]
EXPOSE 8760