#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt

REMOTE_DIR=`whoami`

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_BUILD_DIR=`cat $BUILD_PRIMARY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`

START_TIME=$1
LOGS=$2

mkdir $START_TIME

mkdir $START_TIME/$PRIMARY_HOST
echo "Copying logs from $PRIMARY_HOST"
scp -i $KEY_LOCATION/planetlab-key -r $USER@$PRIMARY_HOST:~/$REMOTE_DIR/$LOGS.log ./$START_TIME/$PRIMARY_HOST/

#for HOST in $SLICE_HOSTS; do
#    echo "Copying logs from $HOST"
#    mkdir $START_TIME/$HOST
#    scp -i $KEY_LOCATION/planetlab-key -r $USER@$HOST:~/$REMOTE_DIR/$LOGS.log ./$START_TIME/$HOST/
#done
#cd ../



