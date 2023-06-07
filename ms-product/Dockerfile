FROM amazoncorretto:11-alpine
VOLUME /tmp
ADD ./target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]