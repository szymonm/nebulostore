#!/bin/bash

# Author: Grzegorz Milka
# Description: Checks if one can connect and run java on hosts in file:
# file_with_hosts and outputs good_hosts to good_output_file.

USAGE="$0 file_with_hosts good_output_file"

if [[ $# -lt 2 ]]; then echo $USAGE; fi

HOST_FILE="$1"
GOOD_FILE="$2"

#How many ssh connection are we to establish in one batch.
MAX_CONCURENCY=5

IFS=$'\n'
HOSTS=$(cat "$1")

echo "" > $GOOD_FILE


CURRENT_CONCURENCY=0
for HOST in $HOSTS
do
    echo "checking $HOST"
    { 
        if { ssh -oConnectTimeout=5 -l mimuw_nebulostore $HOST "java -version"\
            2&>1 >/dev/null; }
        then
            echo $HOST >> $GOOD_FILE
        else
            echo "$HOST failed"
        fi;
    } &
    ((CURRENT_CONCURENCY+=1))
    if [[ $CURRENT_CONCURENCY -eq $MAX_CONCURENCY ]] 
    then 
        wait
        CURRENT_CONCURENCY=0
    fi
done
