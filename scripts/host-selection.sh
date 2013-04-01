#!/bin/bash

# Select at most N hosts from HOST_LIST that have JRE installed and can respond within 2 seconds.

N=300
HOST_LIST="scripts/hosts-all.txt"

USER=mimuw_nebulostore
SSH_OPTION1="StrictHostKeyChecking=no"
SSH_OPTION2="ConnectTimeout=2"

if [ $1 ]; then
  N=$1
fi

i=0
for host in `cat $HOST_LIST`
do
    ssh -o $SSH_OPTION1 -o $SSH_OPTION2 -l $USER $host "java -version &> /dev/null" &> /dev/null
    if [ $? -eq 0 ]
    then
        echo $host
        ((i++))
    fi
    if [ $i -eq $N ]
    then
        break
    fi
done

