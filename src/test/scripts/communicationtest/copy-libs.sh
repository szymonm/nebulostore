#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
KEY_LOCATION_FILE=key-location.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt
TEST_NAME_FILE=test_name.txt

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`
TEST_NAME=`cat $TEST_NAME_FILE`
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

USER=`cat ssh-user.txt`

echo "Copying to $PRIMARY_HOST"
bash scp.sh $PRIMARY_HOST $BUILD_DIR/lib $KEY_LOCATION  lib 

echo "Copying to BootstrapServer: $BOOTSTRAP_SERVER"
bash scp.sh $BOOTSTRAP_SERVER $BUILD_DIR/lib $KEY_LOCATION lib

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST"
    bash scp.sh $HOST $BUILD_DIR/lib $KEY_LOCATION lib
done

echo "Copying done"


