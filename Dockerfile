FROM openjdk
ADD target/elk-spike.jar elk-spike.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "elk-spike.jar"]