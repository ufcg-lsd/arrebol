#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..

sudo nohup mvn spring-boot:run -Drun.profiles=staging &
exit 0
