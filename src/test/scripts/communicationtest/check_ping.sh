#!/bin/bash

SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`

function my_ping {
    echo "Pinging $1"
    ping -c 4 $1
}

my_ping $BOOTSTRAP_SERVER
my_ping $PRIMARY_HOST

for HOST in $SLICE_HOSTS; do
    my_ping $HOST
done
