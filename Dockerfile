FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY spring-boot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9251","-jar","/app.jar"]
