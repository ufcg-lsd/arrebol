#!/bin/bash

start() {
  echo "--> Creating arrebol deployment..."
  docker-compose -f $(pwd)/docker-stack.yml up -d
}

stop() {
  echo "--> Removing arrebol deployment..."
  docker-compose -f $(pwd)/docker-stack.yml down
}

define_param() {
  case $1 in
    start)
      start
      ;;
    stop)
      stop
      ;;
  esac
}

define_param "$@"