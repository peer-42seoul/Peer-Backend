FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/*.jar
ARG LOG4J2_CONFIG_FILE=src/main/resources/log4j2/*.xml
COPY ${JAR_FILE} app.jar
COPY ${LOG4J2_CONFIG_FILE} resources/log4j2/log4j2.xml
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/app.jar"]
