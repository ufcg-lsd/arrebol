FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./bin/arrebol-service-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "-c" , "./config/arrebol.json"]