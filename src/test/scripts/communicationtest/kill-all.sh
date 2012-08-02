#!/bin/bash

KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`

REMOTE_DIR=`whoami`

echo "Killing all instances on remote hosts"

for HOST in $SLICE_HOSTS; do
    echo "Killing instance running on $HOST"
    ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'killall java'
    ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST "rm -rf $REMOTE_DIR/.jxta"
done

echo "Killing instance running on $PRIMARY_HOST"
ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $PRIMARY_HOST 'killall java'

echo "Killing BootstrapServer running on $BOOTSTRAP_SERVER"
ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $BOOTSTRAP_SERVER 'killall java'
