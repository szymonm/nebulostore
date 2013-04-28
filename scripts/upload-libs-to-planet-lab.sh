#!/bin/bash

# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_test_clients number_of_iterations
# Prints "SUCCESS" or "FAILURE"

HOST_LIST="scripts/hosts.txt"

N=200
USER=mimuw_nebulostore
REMOTE_DIR="nebulostore_autodeploy/"
SSH_OPTIONS="StrictHostKeyChecking=no"

echo "BUILDING ..."
./scripts/_build-and-deploy.sh 1 peer > /dev/null

echo "COPYING ..."
i=1
for host in `cat $HOST_LIST`
do
    echo "  "$i": ["`date +"%T"`"]" $host
    rsync -ru ./build/jar/lib $USER@$host:~/$REMOTE_DIR/
    sleep 0.1
    if [ $i -eq $N ]
    then
        break
    else
        ((i++))
    fi
done

