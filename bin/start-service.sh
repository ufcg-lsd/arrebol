#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..

nohup mvn spring-boot:run -Dspring-boot.run.profiles=staging &
exit 0
