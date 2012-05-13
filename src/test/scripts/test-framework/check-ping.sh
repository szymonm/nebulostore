#!/bin/bash

SLICE_HOSTS_FILE=slice-hosts.txt
KEY_LOCATION_FILE=key-location.txt

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE | sort | uniq`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`


IFS=$'\n';
for HOST in $SLICE_HOSTS; do
    echo "Pinging $HOST"
    ping -c 3 $HOST | tail -n 1
done

