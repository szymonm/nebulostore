#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt
TEST_NAME_FILE=test_name.txt

REMOTE_DIR=`whoami`

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`
TEST_NAME=`cat $TEST_NAME_FILE`
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

echo "Cleaning temporary files on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "rm -rf /tmp/*"

echo "Copying data on remote hosts..."
HOST_NO=1
ALL_HOSTS=`cat $SLICE_HOSTS_FILE | wc -l`

echo "Copying to BootstrapServer: $BOOTSTRAP_SERVER"
bash scp.sh $BOOTSTRAP_SERVER $BUILD_DIR $KEY_LOCATION $REMOTE_DIR && \
ssh -i $KEY_LOCATION/planetlab-key -l $USER $BOOTSTRAP_SERVER "ulimit -u 2000;\ cd $REMOTE_DIR/resources/conf/communication; cp JxtaPeer_$RDV.xml JxtaPeer.xml; cp CommunicationPeerServer.xml CommunicationPeer.xml"

echo "Copying to $PRIMARY_HOST"

bash scp.sh $PRIMARY_HOST $BUILD_DIR $KEY_LOCATION $REMOTE_DIR 
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication; cp JxtaPeer_0.xml JxtaPeer.xml" &

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST ($HOST_NO/$ALL_HOSTS)"
    RDV=`expr $HOST_NO % 1`
    bash scp.sh $HOST $BUILD_DIR $KEY_LOCATION $REMOTE_DIR && ssh -i
    $KEY_LOCATION/planetlab-key -l $USER $HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication; cp JxtaPeer_$RDV.xml JxtaPeer.xml"
    ((HOST_NO++))
done

echo "Copying done"
