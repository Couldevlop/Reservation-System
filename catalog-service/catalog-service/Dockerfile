FROM openjdk:21-jdk-slim
LABEL authors="COULIBALY WAOPRON"

COPY target/catalog-service-0.01-SNAPSHOT.jar catalog-service.jar

ENTRYPOINT ["java", "-jar", "/catalog-service.jar"]