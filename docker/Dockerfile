FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./target/arrebol-service-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "-c" , "./config/arrebol.json"]