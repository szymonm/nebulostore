#!/bin/bash
# usage: delay_in_sec

# script is assuming your private key name is planet-lab
# it works for user mimuw_nebulostore

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

START_TIME=`date +"%s"`


# 0. copying data to remote hosts
bash prepare-test.sh

# 1. running jar on all hosts
bash run-all.sh

# 2. sleeping...
echo "Sleeping for $DELAY..."
sleep $DELAY
echo "Waken up"

# 3. killing instances on remote hosts
bash kill-all.sh

# 3. fetching log files from hosts
bash fetch-all.sh $START_TIME "*"

echo "Finished. Take a look at log files in dir $START_TIME"
