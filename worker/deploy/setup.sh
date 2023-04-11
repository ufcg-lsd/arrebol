#!/bin/bash

# A script to setup a worker environment.
# It must run on Ubuntu 16.04 via 'sudo'


readonly LOCAL_NFS_DIR="/nfs"
readonly USAGE="usage: setup.sh <nfs_server> <nfs_server_dir>"

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

set_up_nfs() {
    echo "--> Setting up the NFS Client"
    local NFS_SERVER=$1
    local NFS_SERVER_DIR=$2
    apt-get install -y nfs-common
    mkdir -p ${LOCAL_NFS_DIR}

    echo "--> Mounting ${NFS_SERVER_DIR} into ${LOCAL_NFS_DIR}"

    if mountpoint -q -- "${LOCAL_NFS_DIR}"; then
        echo "Umounting current ${LOCAL_NFS_DIR}..."
        umount -f -l "${LOCAL_NFS_DIR}"
    fi
    mount -t nfs ${NFS_SERVER}:${NFS_SERVER_DIR} ${LOCAL_NFS_DIR}
}

main() {
    if [ ! "$#" -eq 2 ]; then
        echo "Error. ${USAGE}"
        exit 1
    fi

    CHECK_DOCKER_INSTALLATION=$(dpkg -l | grep -c docker-ce)

    if ! [ $CHECK_DOCKER_INSTALLATION -ne 0 ]; then
        install_docker
    else 
        echo "--> Docker its already installed"
    fi

    open_tcp_port

    systemctl daemon-reload
    service docker restart
    set_up_nfs "$@"
}

main "$@"
