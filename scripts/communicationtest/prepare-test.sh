#!/bin/bash

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

. init_util.sh

echo "Cleaning temporary files on $PRIMARY_HOST"
ssh -l $USER $PRIMARY_HOST "rm -rf /tmp/*"

echo "Copying data on remote hosts..."

# Configuration variables
BOOTSTRAP_MODE="server"
BOOTSTRAP_ADDRESS=$BOOTSTRAP_SERVER
CLASS_NAME="org.nebulostore.systest.communication.pingpong.RunPingPong"
PINGPONG_TEST_FUNCTION=""
PEER_NET_ADDRESS=""
SERVER_NET_ADDRESS=$PRIMARY_HOST

HOST_NO=1
ALL_HOSTS=`cat $SLICE_HOSTS_FILE | wc -l`

# Copy and prepare files on target hosts

# Copy and prepare files on BOOTSTRAP_SERVER

BOOTSTRAP_MODE="server"
PINGPONG_TEST_FUNCTION="client"
PEER_NET_ADDRESS="$BOOTSTRAP_SERVER"
echo
echo "Copying to Bootstrap Server: $BOOTSTRAP_SERVER"
bash scp.sh $BOOTSTRAP_SERVER $BUILD_DIR $REMOTE_DIR && \
ssh -l $USER $BOOTSTRAP_SERVER "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/; "\
"./generate_config.py --APP_KEY=$HOST_NO --BOOTSTRAP_MODE=$BOOTSTRAP_MODE "\
"--BOOTSTRAP_ADDRESS=$BOOTSTRAP_SERVER "\
"--CLASS_NAME=$CLASS_NAME --COMM_ADDRESS=$HOST_NO --PEER_ID=$HOST_NO "\
"--PEER_NET_ADDRESS=$PEER_NET_ADDRESS "\
"--PINGPONG_TEST_FUNCTION=$PINGPONG_TEST_FUNCTION "\
"--SERVER_NET_ADDRESS=$SERVER_NET_ADDRESS < Peer.xml.template > Peer.xml"
((HOST_NO++))

# Copy and prepare files on PRIMARY_HOST

BOOTSTRAP_MODE="client"
PINGPONG_TEST_FUNCTION="server"
PEER_NET_ADDRESS=$PRIMARY_HOST
echo
echo "Copying to Primary Host: $PRIMARY_HOST"
bash scp.sh $PRIMARY_HOST $BUILD_DIR $REMOTE_DIR && \
ssh -l $USER $PRIMARY_HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/; "\
"./generate_config.py --APP_KEY=$HOST_NO --BOOTSTRAP_MODE=$BOOTSTRAP_MODE "\
"--BOOTSTRAP_ADDRESS=$BOOTSTRAP_SERVER "\
"--CLASS_NAME=$CLASS_NAME --COMM_ADDRESS=$HOST_NO --PEER_ID=$HOST_NO "\
"--PEER_NET_ADDRESS=$PEER_NET_ADDRESS "\
"--PINGPONG_TEST_FUNCTION=$PINGPONG_TEST_FUNCTION "\
"--SERVER_NET_ADDRESS=$SERVER_NET_ADDRESS < Peer.xml.template > Peer.xml"
((HOST_NO++))

# Copy and prepare files on SLICE_HOSTS

BOOTSTRAP_MODE="client"
PINGPONG_TEST_FUNCTION="client"
IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    PEER_NET_ADDRESS=$HOST
    echo
    echo "Copying to $HOST ($(($HOST_NO - 2))/$ALL_HOSTS)"
    RDV=`expr $HOST_NO % 1`
    bash scp.sh $HOST $BUILD_DIR $REMOTE_DIR && \
    ssh -l $USER $HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/; "\
    "./generate_config.py --APP_KEY=$HOST_NO --BOOTSTRAP_MODE=$BOOTSTRAP_MODE "\
    "--BOOTSTRAP_ADDRESS=$BOOTSTRAP_SERVER "\
    "--CLASS_NAME=$CLASS_NAME --COMM_ADDRESS=$HOST_NO --PEER_ID=$HOST_NO "\
    "--PEER_NET_ADDRESS=$PEER_NET_ADDRESS "\
    "--PINGPONG_TEST_FUNCTION=$PINGPONG_TEST_FUNCTION "\
    "--SERVER_NET_ADDRESS=$SERVER_NET_ADDRESS < Peer.xml.template > Peer.xml"
    ((HOST_NO++))
done

wait

echo "Copying done"
