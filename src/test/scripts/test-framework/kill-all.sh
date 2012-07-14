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
BDB_HOLDER="host1.planetlab.informatik.tu-darmstadt.de"

REMOTE_DIR=`whoami`



echo "Killing all instances on remote hosts"


echo "Killing instance running on $PRIMARY_HOST"
ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $PRIMARY_HOST 'killall java'


for HOST in $SLICE_HOSTS; do
    echo "Killing instance running on $HOST"
    ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'killall java'
    ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST "rm -rf $REMOTE_DIR/.jxta"
done

echo "Killing BDB's Holder running on $BDB_HOLDER"
ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $BDB_HOLDER 'killall java'

echo "Killing BootstrapServer running on $BOOTSTRAP_SERVER"
ssh -o ConnectTimeout=5 -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $BOOTSTRAP_SERVER 'killall java'
