#!/bin/bash
# usage: delay_in_sec

# script is assuming your private key name is planet-lab
# it works for user mimuw_nebulostore

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 {DELAY}"
    exit 1;
fi

BUILD_LOCATION_FILE=build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
TEST_NAME_FILE=test_name.txt

REMOTE_DIR=`whoami`

DELAY=$1

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
TEST_NAME=`cat $TEST_NAME_FILE`
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

START_TIME=`date +"%s"`

# -1. Cleaning previous logs
bash clean-logs.sh

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
#bash fetch-all.sh $START_TIME "*"

echo "Finished. Take a look at log files in dir $START_TIME"
