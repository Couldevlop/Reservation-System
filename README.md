# Système de réservation

 ## Introduction
 Le projet Reservation System est une application distribuée basée sur une architecture de microservices. Il permet aux utilisateurs de réserver des voitures en vérifiant leur disponibilité et en effectuant des paiements, avec une gestion des événements via Kafka. Les services sont coordonnés via Eureka pour la découverte et Spring Cloud Gateway comme point d’entrée unique.

 ## Architecture
 L'architecture est composée des éléments suivants :

- **Eureka Server** : Serveur de découverte pour enregistrer et localiser les microservices.
- **API Gateway** : Point d’entrée unique pour router les requêtes vers les microservices via Eureka.
- **Catalog Service** : Gère le catalogue des voitures (création, mise à jour, suppression) et diffuse les changements via Kafka.
- **Reservation Service** : Gère les réservations, vérifie la disponibilité des voitures via catalog-service, et effectue les paiements via payment-service.
- **Payment Service** : Gère les paiements et valide les numéros de carte.
- **Kafka** : Système de messagerie pour diffuser les événements (ex. mise à jour ou suppression de voitures, état des réservations).
- **Zookeeper** : Coordination pour Kafka.
- **Kafka UI (kcat)** : Outil web pour interagir avec Kafka (consommer/produire des messages).

## Diagramme d'Architecture
           +----------------+       +----------------+
          |  Eureka Server |<----->|  API Gateway   |
          |    :8761       |       |    :8080       |
          +----------------+       +----------------+
                                     |       |
                                     v       v
          +----------------+       +----------------+       +----------------+
          | Catalog Service|<----->|Reservation Serv.|<----->| Payment Service|
          |    :8081       |       |    :8082       |       |    :8083       |
          +----------------+       +----------------+       +----------------+
                |                        |                        |
                v                        v                        v
          +----------------+       +----------------+       +----------------+
          |     Kafka      |<----->|   Zookeeper    |       |    Kafka UI    |
          |  :9092/:29092 |       |    :2181       |       |    :8089       |
          +----------------+       +----------------+       +----------------+

## Microservices

### Eureka
- **Rôle** : Serveur de découverte pour enregistrer tous les microservices.
- **Port** : 8761.
- **Dépendances** : Spring Cloud Netflix Eureka Server.

### API Gateway
- **Rôle** : Point d’entrée unique, route les requêtes vers les microservices via Eureka.
- **Port** : 8080.
- **Dépendances** : Spring Cloud Gateway, Eureka Client.

### Catalog Service
 - **Rôle** : Gère le catalogue des voitures (CRUD) et diffuse les changements via Kafka. 
 - **Port** : 8081.
 - **Dépendances** : Spring Boot, JPA, Kafka, Eureka Client.

### Reservation Service
 - **Rôle** : Gère les réservations, vérifie la disponibilité via catalog-service, effectue les paiements via payment-service, et diffuse les événements via Kafka.
 - **Port** : 8082
 - **Dépendances** : Spring Boot, JPA, Kafka, Eureka Client, Feign Client, Resilience4j.

### Payment Service
 - **Rôle** : Gère les paiements et valide les numéros de carte.
 - **Port** : 8083
 - **Dépendances** : Spring Boot, JPA, Eureka Client.


## Infrastructure

### Kafka
- **Rôle** : Système de messagerie pour diffuser les événements entre services.
- **Ports** : 9092 (hôte local), 29092 (réseau Docker interne).

### Zookeeper
- **Rôle** : Coordination pour Kafka.
- **Port** : 2181.

### Kafka UI
- **Rôle** : Interface web pour visualiser et gérer les topics, messages, et brokers Kafka.
- **Port** : 8089 (mappé à 8080 dans le conteneur).


## Endpoints et Résultats

### Via API Gateway (http://localhost:8080)

#### Catalog Service

##### Créer une Voiture :
- **Requête** : POST /api/v1/cars
- **Body** : 
  ```json
  {
    "marque": "Toyota",
    "modele": "Camry",
    "annee": 2023,
    "prix": 25000
  }
  
- **Réponse** : 201 Created
  ```json
  {
    "id": "xxx",
    "name": "Toyota",
    "available": true
  }

##### Obtenir  une Voiture :
- **Requête** : GET /api/v1/cars/{id}
  }
  
- **Réponse** : 200 OK
  ```json
  {
    "id": "xxx",
    "name": "Toyota",
    "available": true
  }
  
##### Mettre à jour une Voiture :
- **Requête** : PUT /api/v1/cars/{id}
- **Body** : 
  ```json
  {
    "name": "Toyota Updated",
    "available": false
  }
  
- **Réponse** : 200 OK
  ```json
  {
    "id": "xxx",
    "name": "Toyota Updated",
    "available": false
  }

##### Supprimer une Voiture :
- **Requête** : PUT /api/v1/reservations/{id}  
- **Réponse** : 200 OK

  
### Via API Gateway (http://localhost:8080)

#### Reservation Service

##### Créer une Réservation :
- **Requête** : POST /api/v1/reservations
- **Body** : 
  ```json
  {
    "carId": "xxx",
    "userId": "user1",
    "status": "PENDING",
    "cardNumber": "4123456789012345",
    "amount": 750.0
  }
  
- **Réponse** (si voiture disponible et paiement réussi) : 201 Created
  ```json
  {
    "id": "yyy",
    "carId": "xxx",
    "userId": "user1",
    "status": "CONFIRMED"
  }
- **Réponse** (si voiture indisponible) : 400 Bad Request
  ```json
  {
    "status": 400,
    "error": "Bad Request",
    "message": "Vehicle with ID xxx is not available"
  }
  
##### Obtenir une réservation :
- **Requête** : GET /api/v1/reservations/{id}

- **Réponse** : 200 OK
  ```json
  {
    "id": "xxx",
    "carId": "xxx",
    "userId": "1",
    "status": "PENDING"
  }
  
##### Mettre à jour une réservation :
- **Requête** : PUT /api/v1/reservations/{id}
- **Body** :
  ```json
  {
    "carId": "xxx",
    "userId": "xxx",
    "status": "CANCELLED",
    "cardNumber": "XXXX",
    "amount": 90000
  }
- **Réponse** : 200 OK
  ```json
  {
    "id": "xxx",
    "carId": "xxx",
    "userId": "xxx",
    "status": "CANCELLED",
    "cardNumber": "XXXX",
    "amount": 90000
  }
  
##### Supprimer une reservation :
- **Requête** : PUT /api/v1/reservations/{id}  
- **Réponse** : 200 OK


### Via API Gateway (http://localhost:8080)

#### Payment Service

##### Créer un Paiement :
- **Requête** : POST /api/v1/payments
- **Body** :
  ```json
  {
    "reservationId": "xxxxx",
    "amount": 9000.0,
    "cardNumber": "xxxxx",
    "createdAt": "2025-02-24T22:23:27.752Z",
    "successful": true
  }
  
- **Réponse** : 201 Created
  ```json
  {
    "id": "xxxx",
    "reservationId": "xxxx",
    "amount": 9000.0,
    "cardNumber": "xxxx",
    "createdAt": "2025-02-24T22:23:27.752Z",
    "successful": true
  }
  
##### Obtenir un Paiement :
- **Requête** : GET /api/v1/payments/{id}
- **Réponse** : 200 OK
  ```json
  {
    "id": "xxxx",
    "reservationId": "xxxx",
    "amount": 9000.0,
    "cardNumber": "xxxx",
    "createdAt": "2025-02-24T22:23:27.752Z",
    "successful": true
  }
  
##### Mettre à jour un paiement :
- **Requête** : PUT /api/v1/payments/{id}
- **Body** :
  ```json
  {
    "reservationId": "1",
    "amount": 15000.0,
    "cardNumber": "4234567891044451",
    "createdAt": "2025-02-22T22:37:34.720852",
    "successful": true
  }
- **Réponse** : 200 OK
  ```json
  {
    "id": "132df2df-04b2-42e6-93de-24a10295ff66",
    "reservationId": "1",
    "amount": 15000.0,
    "cardNumber": "4234567891044451",
    "createdAt": "2025-02-22T22:39:18.238893",
    "successful": true
  }
##### Supprimer une réservation :
- **Requête** : DELETE /api/v1/reservations/{id}
- **Réponse** : 204 No Content


### Configuration des Fichiers

#### Dockerfile par Microservice

Chaque microservice utilise un Dockerfile similaire :

* **Eureka Server** : eureka-server/Dockerfile
* **API Gateway** : api-gateway/Dockerfile
* **Catalog Service** : catalog-service/Dockerfile
* **Reservation Service** : reservation-service/Dockerfile
* **Payment Service** : payment-service/Dockerfile

Exemple générique :

**FROM openjdk:21-jdk-slim** 
**COPY target/<service-name>-0.0.1-SNAPSHOT.jar <service-name>.jar**  
**ENTRYPOINT ["java", "-jar", "/<service-name>.jar"]**  


### Docker Compose


**\Reservation-system\reservation-service\reservation-service\docker-compose.yml**

### Configuration Locale vs Production

#### Local

##### Kafka

* **KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,INTERNAL://kafka:29092** : Double écoute pour IntelliJ (localhost:9092) et Docker (kafka:29092).
* **Ports exposés** : 9092:9092, 29092:29092.

##### Kafka UI

* **ports: "8089:8080"** : Accessible via localhost:8089.
* **KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS**: kafka:29092 : Utilise le réseau interne Docker.

##### Eureka Server:

* **defaultZone**: http://localhost:8761/eureka/ dans tous les services.

##### Microservices

* Utilisent localhost:9092 pour Kafka et localhost:8761 pour Eureka, avec des ports fixes.

#### Production


## Tests Unitaires

* **Payment Service** : Couvre createPayment, getPaymentById, getAllPayments, updatePayment, deletePayment.
* **Catalog Service** : Couvre createCar, getCarById, getAllCars, updateCar, deleteCar.
* **Approche** : Utilise Mockito pour mocker les dépendances (PaymentRepository, CarRepository, KafkaTemplate).

##### Kafka

* **Changement : Modifier KAFKA_ADVERTISED_LISTENERS pour un domaine public ou une IP**
    `KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka.example.com:9092`
* **Sécurité : Ajouter SSL/TLS :**

```yaml
environment:
  KAFKA_ADVERTISED_LISTENERS: SSL://kafka.example.com:9093
  KAFKA_SSL_KEYSTORE_LOCATION: /etc/kafka/secrets/kafka.keystore.jks
  KAFKA_SSL_KEYSTORE_PASSWORD: <password>
  KAFKA_SSL_KEY_PASSWORD: <password>
  KAFKA_SSL_TRUSTSTORE_LOCATION: /etc/kafka/secrets/kafka.truststore.jks
  KAFKA_SSL_TRUSTSTORE_PASSWORD: <password>
volumes:
  - /path/to/secrets:/etc/kafka/secrets
ports:
  - "9093:9093"



 Instructions d'Installation et d'Exécution

  - Local (via IntelliJ et Docker Compose)

  - Kafka UI
    *Changement de Port: Utiliser un port sécurisé ou un reverse proxy :

```yaml
environment:
  KAFKA_CLUSTERS_0_NAME: prod
  KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka.example.com:9093
  KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper.example.com:2181
  KAFKA_CLUSTERS_0_PROPERTIES_SECURITY_PROTOCOL: SSL
  KAFKA_CLUSTERS_0_PROPERTIES_SSL_TRUSTSTORE_LOCATION: /etc/kafka/secrets/kafka.truststore.jks
  KAFKA_CLUSTERS_0_PROPERTIES_SSL_TRUSTSTORE_PASSWORD: <password>
volumes:
  - /path/to/secrets:/etc/kafka/secrets


#### Kafka, Zookeeper, Kafka UI :

```bash
cd D:\OPENLAB\Reservation-system\reservation-service\reservation-service
docker-compose up -d





  
