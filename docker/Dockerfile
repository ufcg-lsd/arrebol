FROM openjdk:8

ARG ARREBOL_BRANCH

RUN \
    apt-get update -y && \
    apt-get install -y maven curl git lsof vim

ENV HOME /root

WORKDIR /root

RUN \
    git clone -b $ARREBOL_BRANCH https://github.com/ufcg-lsd/arrebol.git --single-branch && \
    (cd arrebol && mvn clean install -DskipTests)

CMD ["/bin/bash", "-c", "cd /root/arrebol && mvn spring-boot:run"]