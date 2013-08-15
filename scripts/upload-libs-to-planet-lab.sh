#!/bin/bash

# Uploads dependency libraries to nebulostore_autodeploy folder on hosts listed
# in nodes/hosts.txt file.

# Usage:
# bash upload-libs-to-planet-lab.sh [-n NUMBER_OF_HOSTS=200] [-v]
#   -v sets the verbose flag

EXEC_DIR=$(pwd)
cd $(dirname $0)

HOST_LIST="nodes/hosts.txt"

while getopts ":n:v" OPTION
do
  case $OPTION in
    n) N=$OPTARG;;
    v) RSYNC_OPTIONS="-rvu --size-only";;
    *) ARG=$(($OPTIND-1))
       echo "Unknown option option chosen: ${!ARG}."
  esac
done

: ${N=200}
USER=mimuw_nebulostore
REMOTE_DIR="nebulostore_autodeploy/"
SSH_OPTIONS="StrictHostKeyChecking=no"
: ${RSYNC_OPTIONS="-ru --size-only"}

echo "BUILDING ..."
./_build-and-deploy.sh -p 1 -m peer > /dev/null

echo "COPYING ..."
i=1
for HOST in $(cat $HOST_LIST | head -n $N)
do
    echo "  "$((i++))": ["`date +"%T"`"]" $HOST
    rsync -e "ssh -o $SSH_OPTIONS"\
        $RSYNC_OPTIONS ../build/jar/lib $USER@$HOST:~/$REMOTE_DIR/
done

cd ${EXEC_DIR}
