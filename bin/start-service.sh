#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..

mvn clean install
nohup mvn spring-boot:run -Drun.profiles=staging &
exit 0