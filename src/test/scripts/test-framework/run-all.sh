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

echo "Running jar on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $PRIMARY_HOST "cd $REMOTE_DIR; java -Xmx1024m -Xms64m -jar Nebulostore.jar > std.out.log 2> std.err.log &"

sleep 2


for HOST in $SLICE_HOSTS; do
   echo "Running jar on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST "cd $REMOTE_DIR; java -Xmx512m -Xms32m -jar Nebulostore.jar > std.out.log 2> std.err.log &" & 
done


sleep 5

