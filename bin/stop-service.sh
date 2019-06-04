#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..

if [ ! -f ./bin/shutdown.pid ]; then
    echo "shutdown.pid not found!"
else
    kill $(cat ./bin/shutdown.pid)
fi
