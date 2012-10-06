#!/bin/bash

. init_util.sh
TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

BUILD_LOCATION_FILE=build-location.txt
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

echo "Cleaning temporary files on $PRIMARY_HOST"
ssh -l $USER $PRIMARY_HOST "rm -rf /tmp/*"

echo "Copying data on remote hosts..."
HOST_NO=1
ALL_HOSTS=`cat $SLICE_HOSTS_FILE | wc -l`

echo "Copying to BootstrapServer: $BOOTSTRAP_SERVER"
bash scp.sh $BOOTSTRAP_SERVER $BUILD_DIR $REMOTE_DIR && ssh -l $USER $BOOTSTRAP_SERVER "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication; cp CommunicationPeerServer.xml CommunicationPeer.xml" 

echo "Copying to $PRIMARY_HOST"
bash scp.sh $PRIMARY_HOST $BUILD_DIR $REMOTE_DIR 
ssh -l $USER $PRIMARY_HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication"

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST ($HOST_NO/$ALL_HOSTS)"
    RDV=`expr $HOST_NO % 1`
    bash scp.sh $HOST $BUILD_DIR $REMOTE_DIR && \
        ssh -l $USER $HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication" 
    ((HOST_NO++))
done

wait

echo "Copying done"
