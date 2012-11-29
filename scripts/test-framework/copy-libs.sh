#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
SLICE_HOSTS_FILE=all-hosts.txt
KEY_LOCATION_FILE=key-location.txt

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`


IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST"
    bash scp.sh $HOST $BUILD_DIR/lib $KEY_LOCATION lib
done

echo "Copying done"


