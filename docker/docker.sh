#!/bin/bash

readonly ARREBOL_REPO=ufcglsd/arrebol

build() {
  local DOCKERFILE_DIR=docker/Dockerfile
  local TAG="${1-latest}"
  mvn clean install -DskipTests
  docker build --tag "${ARREBOL_REPO}":"${TAG}" \
          --file "${DOCKERFILE_DIR}" .
}

run() {
  local TAG="${1-latest}"
  local PORT="${2-8080}"
  local CONTAINER_NAME="arrebol"
  docker run -dit \
    --name "${CONTAINER_NAME}" \
    -p ${PORT}:8080 \
    -v "$(pwd)"/src/main/java/resources/arrebol.json:/service/config/arrebol.json \
    "${ARREBOL_REPO}":"${TAG}"
}

publish() {
  local tag="${1:-latest}"
  docker push "${ARREBOL_REPO}":"${tag}"
}

define_params() {
  case $1 in
    build)
      build $2
      ;;
    run)
      run $2 $3
      ;;
    publish)
      publish $2
  esac
}

define_params "$@"