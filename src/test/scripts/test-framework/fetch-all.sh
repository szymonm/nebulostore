#!/bin/bash

KEY_LOCATION_FILE=key-location.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt

REMOTE_DIR=`whoami`

KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`

START_TIME=$1
LOGS=$2

mkdir $START_TIME

mkdir $START_TIME/$PRIMARY_HOST
echo "Copying logs from $PRIMARY_HOST"
scp -i $KEY_LOCATION/planetlab-key -r $USER@$PRIMARY_HOST:~/$REMOTE_DIR/$LOGS.log ./$START_TIME/$PRIMARY_HOST/
