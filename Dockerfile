FROM openjdk
ADD target/elk-spike.jar elk-spike.jar
ADD elastic-apm-agent-1.24.0.jar elastic-apm-agent-1.24.0.jar
ADD elasticapm.properties elasticapm.properties
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:elastic-apm-agent-1.24.0.jar", "-jar", "elk-spike.jar"]
#ENTRYPOINT ["java", "-jar", "elk-spike.jar"]

