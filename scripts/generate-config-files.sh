#!/bin/bash

# Generate config files for test peers. First peer is bootstrap server.
# Last peer is testing peer.
# Optionally, the following parameters might be provided:
#   test_class_name number_of_peers number_of_test_iterations bootstrap_address

TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_ITER=3
BOOTSTRAP_ADDRESS="localhost"

PEERNAME="org.nebulostore.systest.TestingPeer"

COMMON_ARGS="--BOOTSTRAP_PORT=10201 --BOOTSTRAP_TOMP2P_PORT=10301"

if [ $1 ]; then
  TESTNAME=$1
  PEER_NUM=$2
  TEST_ITER=$3
  BOOTSTRAP_ADDRESS=$4
fi

# Generate test list.
for i in `seq 1 $TEST_ITER`
do
    if [ $i -ne 1 ]
    then
        CONCAT=$CONCAT,
    fi
    CONCAT=$CONCAT$TESTNAME
done

# Configure peers.
for i in `seq 1 $PEER_NUM`
do
    # Make first peer bootstrap server and storage holder.
    if [ $i -eq 1 ]
    then
        BOOTSTRAP_MODE="server"
        BDB_TYPE="storage-holder"
    else
        BOOTSTRAP_MODE="client"
        BDB_TYPE="proxy"
    fi

    # Make last peer test server.
    if [ $i -eq $PEER_NUM ]
    then
        IS_SERVER="true"
    else
        IS_SERVER="false"
    fi

    padded=`printf "%02d" $i`
    ./resources/conf/generate_config.py $COMMON_ARGS --APP_KEY=$i$i --CLASS_NAME=$PEERNAME --TEST_LIST=$CONCAT\
         --BOOTSTRAP_MODE=$BOOTSTRAP_MODE --CLI_PORT=101$padded --TOMP2P_PORT=103$padded --BDB_TYPE=$BDB_TYPE\
         --BOOTSTRAP_ADDRESS=$BOOTSTRAP_ADDRESS --IS_SERVER=$IS_SERVER < ./resources/conf/Peer.xml.template > Peer.xml.$i
done

