#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..

readonly ARREBOL_PORT=8080

kill -9 $(lsof -ti tcp:$ARREBOL_PORT)