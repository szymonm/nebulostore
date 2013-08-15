#!/bin/bash
# Usage host-selection.sh [GOOD_HOST_LIMIT]

# Select at most GOOD_HOST_LIMIT hosts from HOST_LIST that have JRE installed
# and can respond within 2 seconds.

export GOOD_HOST_LIMIT=${1-300}
HOST_LIST="hosts-all.txt"

export USER=mimuw_nebulostore
export SSH_OPTIONS="-o ConnectTimeout=2 -o StrictHostKeyChecking=no"

export i=0

function TEST_HOST() {
    HOST=$1
    if [ $i -eq ${GOOD_HOST_LIMIT} ]
    then
        return 0;
    fi
    ssh ${SSH_OPTIONS} -l $USER $HOST "java -version &> /dev/null" &>/dev/null
    if [ $? -eq 0 ]
    then
        echo $HOST
        ((i++))
    fi
}
export -f TEST_HOST

EXEC_DIR=$(pwd)
cd $(dirname $0)

cat $HOST_LIST | xargs -P 15 -n 1 -i bash -c 'TEST_HOST "$1"' _ {}

cd ${EXEC_DIR}
