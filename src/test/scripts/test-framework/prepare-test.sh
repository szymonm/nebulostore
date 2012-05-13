#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt

REMOTE_DIR=`whoami`

DELAY=$1

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_BUILD_DIR=`cat $BUILD_PRIMARY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`

echo "Cleaning temporary files on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $PRIMARY_HOST "rm -rf /tmp/*"


echo "Copying data on remote hosts..."

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST"
    bash scp.sh $HOST $BUILD_DIR $KEY_LOCATION $REMOTE_DIR &
    sleep 3
done


sleep 5
echo "Copying to $PRIMARY_HOST"

bash scp.sh $PRIMARY_HOST $PRIMARY_BUILD_DIR $KEY_LOCATION $REMOTE_DIR
sleep 10

echo "Copying done"

