FROM openjdk:8

#Define working directory
WORKDIR /service

COPY ./bin/arrebol-service-1.0.jar app.jar
COPY ./bin/lib  lib/

ENTRYPOINT ["java", "-jar", "app.jar", "-c" , "./config/arrebol.json"]