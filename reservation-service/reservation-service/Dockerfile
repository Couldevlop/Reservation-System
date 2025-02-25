FROM openjdk:21-jdk-slim
LABEL authors="COULIBALY Waopron"
COPY target/reservation-service-0.0.1-SNAPSHOT.jar reservation-service.jar
ENTRYPOINT ["java", "-jar", "/reservation-service.jar"]