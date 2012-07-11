#!/bin/bash
#Author: Grzegorz Milka

KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
REN_HOSTS_FILE=rendez-hosts.txt

REMOTE_DIR=`whoami`

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
REN_HOSTS=`cat $REN_HOSTS_FILE`

echo "Cleaning logs on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "rm $REMOTE_DIR/*.log"

for HOST in $SLICE_HOSTS; do 
    echo "Cleaning logs on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "rm $REMOTE_DIR/*.log"

for HOST in $REN_HOSTS; do 
    echo "Cleaning logs on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "rm $REMOTE_DIR/*.log"
