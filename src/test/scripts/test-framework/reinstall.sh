#!/bin/bash

CONNECTION_TIMEOUT=2

BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
SLICE_HOSTS_FILE=all-hosts-ext.sort.txt
KEY_LOCATION_FILE=key-location.txt


PRIMARY_BUILD_DIR=`cat $BUILD_PRIMARY_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 

        echo "Installing at $HOST"
#        ssh -t -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'mkdir ~/install'
#        scp -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -r ./install/jre-6u31-linux-i586-rpm.bin  mimuw_nebulostore@$HOST:~/install/
#        ssh -t -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'cd install && sudo bash ./jre-6u31-linux-i586-rpm.bin'

        scp -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -r $PRIMARY_BUILD_DIR/lib  mimuw_nebulostore@$HOST:~/

        echo "Done"
done
