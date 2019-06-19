#!/bin/bash

set +e

for i in "$@"
do
	case $i in
	    -tsf=*|--task_filepath=*)
	    __TASK_SCRIPT_FILEPATH="${i#*=}"
	    shift
	    ;;
	    -d|--debug)
	    DEBUG=YES
	    shift
	    ;;
	    *)
	        # unknown option
	    ;;
	esac
done

if [ ! -f "$__TASK_SCRIPT_FILEPATH" ];
then
	echo "$__TASK_SCRIPT_FILEPATH is not a file"
	exit 17
fi

TS_FILENAME=$(basename $__TASK_SCRIPT_FILEPATH)

__EXIT_CODES=/tmp/$TS_FILENAME.ec
rm $__EXIT_CODES
touch $__EXIT_CODES

__COMMANDS=/tmp/$TS_FILENAME.cmds
rm $__COMMANDS
touch $__COMMANDS

if [ -n "$DEBUG" ];
then
	rm /tmp/TS_FILENAME.out
	exec 1> /tmp/$TS_FILENAME.out
	rm /tmp/TS_FILENAME.err
	exec 2> /tmp/$TS_FILENAME.err
fi

while IFS= read -r __line || [ -n "$__line" ]; do
	echo $__line >> $__COMMANDS
	set +e
    eval $__line
    echo "$?" >> $__EXIT_CODES
done < $__TASK_SCRIPT_FILEPATH