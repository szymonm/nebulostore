#!/bin/bash

# script is assuming your private key name is planet-lab
# it works for user mimuw_nebulostore

BUILD_LOCATION_FILE=build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
REMOTE_DIR=`whoami`


DELAY=350

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`

START_TIME=`date +"%s"`


# 0. copying data to remote hosts
echo "Copying data on remote hosts..."
IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST"
    bash scp.sh $HOST $BUILD_DIR $KEY_LOCATION $REMOTE_DIR
done
echo "Copying done"

# 1. running jar on all hosts
for HOST in $SLICE_HOSTS; do
    echo "Running jar on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST "cd $REMOTE_DIR; java -jar Nebulostore.jar > std.out.log 2> std.err.log &"
done

# 2. sleeping...
echo "Sleeping for $DELAY..."
sleep $DELAY
echo "Waken up"

# 3. killing instances on remote hosts
echo "Killing all instances on remote hosts"
for HOST in $SLICE_HOSTS; do
    echo "Killing instance running on $HOST"
    ssh -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'killall java'
done

# 3. fetching log files from hosts
mkdir $START_TIME
for HOST in $SLICE_HOSTS; do
    echo "Copying logs from $HOST"
    mkdir $START_TIME/$HOST
    scp -i $KEY_LOCATION/planetlab-key -r mimuw_nebulostore@$HOST:~/$REMOTE_DIR/*.log ./$START_TIME/$HOST/
done
cd ../

echo "Finished. Take a look at log files in dir $START_TIME"
