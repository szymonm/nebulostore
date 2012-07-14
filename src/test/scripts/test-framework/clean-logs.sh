#!/bin/bash
#Author: Grzegorz Milka

KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt

REMOTE_DIR=`whoami`

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`

BDB_HOLDER="host1.planetlab.informatik.tu-darmstadt.de"

echo "Cleaning logs on $BDB_HOLDER"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $BDB_HOLDER "rm $REMOTE_DIR/*.log"

echo "Cleaning logs on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "rm $REMOTE_DIR/*.log"

for HOST in $SLICE_HOSTS; do 
    echo "Cleaning logs on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "rm $REMOTE_DIR/*.log"
done

echo "Cleaning logs on $BOOTSTRAP_SERVER"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $BOOTSTRAP_SERVER "rm $REMOTE_DIR/*.log"
