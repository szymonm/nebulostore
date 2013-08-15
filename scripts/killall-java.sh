#!/bin/bash

# Script that kills java processes at all hosts on the list.

HOST_LIST="./nodes/hosts.txt"
USER=mimuw_nebulostore
SSH_OPTIONS="StrictHostKeyChecking=no"

EXEC_DIR=$(pwd)
cd $(dirname $0)


for host in `cat $HOST_LIST`
do
    echo "  " $host
    ssh -o $SSH_OPTIONS -l $USER $host "killall java"
done

cd ${EXEC_DIR}
