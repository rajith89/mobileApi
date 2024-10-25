# Step 1 : Build and execute Mobile API service
FROM azul/zulu-openjdk-alpine:11.0.11
MAINTAINER ICTA
COPY target/udipoc-mobile-service.jar udipoc-mobile-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/udipoc-mobile-service.jar"]