#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..
BIN_PATH=`pwd`/bin

echo "Parando o arrebol ===========" > $BIN_PATH/db_cleaner.log 
kill `cat $BIN_PATH/shutdown.pid` >> $BIN_PATH/db_cleaner.log

echo "Limpando o banco ============" >> $BIN_PATH/db_cleaner.log 
PGPASSWORD=@rrebol psql -h localhost -p 5432 arrebol postgres -a -f $BIN_PATH/db_cleaner.sql >> $BIN_PATH/db_cleaner.log

echo "Executando o arrebol ========" >> $BIN_PATH/db_cleaner.log 
sh $BIN_PATH/start-service.sh >> $BIN_PATH/db_cleaner.log

echo "Finalizando script ==========" >> $BIN_PATH/db_cleaner.log 
exit 0
