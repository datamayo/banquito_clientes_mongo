FROM eclipse-temurin:17-jre-focal
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} banquito.jar
#EXPOSE 9003
ENTRYPOINT ["java","-jar","/banquito.jar"]
