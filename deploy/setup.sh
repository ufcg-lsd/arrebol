#!/bin/bash

# A script to setup a swarm manager environment.
# It expects to be run on Ubuntu 16.04 via 'sudo'

install_docker() {
    echo "--> Installing docker"
    apt update

    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        software-properties-common
    
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

    apt-key fingerprint 0EBFCD88

    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) \
    stable"

    apt-get update

    apt-get install -y docker-ce
}

install_docker_compose() {
    echo "--> Installing Docker Compose..."
    sudo curl -sL "https://github.com/docker/compose/releases/download/1.25.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
}

run() {
    DOCKER_INSTALLED=$(dpkg -l | grep -c docker-ce)

    if ! [ $DOCKER_INSTALLED -ne 0 ]; then
        install_docker
    else 
        echo "--> Docker already installed!"
    fi
    install_docker_compose
}

run