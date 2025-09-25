FROM openjdk:21-jre-slim
VOLUME /tmp
COPY ${JAR_FILE} fragmentlm-distributor.jar
ENTRYPOINT ["java","-jar","/fragmentlm-distributor.jar"]
