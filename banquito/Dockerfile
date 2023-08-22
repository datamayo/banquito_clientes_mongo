FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} banquito.jar
ENTRYPOINT ["java","-jar","/banquito.jar"]
EXPOSE 8760