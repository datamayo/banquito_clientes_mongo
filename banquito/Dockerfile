FROM eclipse-temurin:17-jre-focal
#ARG JAR_FILE=target/*.jar
COPY target/banquito.jar banquito.jar
ENTRYPOINT ["java","-jar","/banquito.jar"]
#EXPOSE 8760