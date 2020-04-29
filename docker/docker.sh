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
    -v "$(pwd)"/src/main/java/resources/application.properties:/service/config/application.properties \
    "${ARREBOL_REPO}":"${TAG}"
}

publish() {
  local tag="${1:-latest}"
  docker push "${ARREBOL_REPO}":"${tag}"
}

define_params() {
  case "$@" in
    build) shift
      build "$@"
      ;;
    run) shift
      run "$@"
      ;;
    publish) shift
      publish "$@"
      ;;
  esac
}

define_params "$@"