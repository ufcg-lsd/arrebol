#!/bin/bash

# A script to setup a worker environment.
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

open_tcp_port() {
    echo "--> Setting up the TCP port"

    local PROPERTY_PATTERN="ExecStart"
    local PROPERTY_VALUE="/usr/bin/dockerd -H fd:// -H=tcp://0.0.0.0:5555 --containerd=/run/containerd/containerd.sock"

    local DOCKER_CONF_FILE="/lib/systemd/system/docker.service"

    sed -i "s@.*${PROPERTY_PATTERN}=.*@${PROPERTY_PATTERN}=${PROPERTY_VALUE}@" $DOCKER_CONF_FILE
}

main() {
    CHECK_DOCKER_INSTALLATION=$(dpkg -l | grep -c docker-ce)

    if ! [ $CHECK_DOCKER_INSTALLATION -ne 0 ]; then
        install_docker
    else 
        echo "--> Docker its already installed"
    fi

    open_tcp_port

    systemctl daemon-reload
    service docker restart   
}

main
