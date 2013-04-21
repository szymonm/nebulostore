#!/bin/bash

# Generate config files for test peers. First peer is bootstrap server.
# Last peer is testing peer.
# Optionally, the following parameters might be provided:
#   peer_class_name test_class_name number_of_peers number_of_test_clients number_of_test_iterations bootstrap_address

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_CLIENTS_NUM=2
TEST_ITER=3
BOOTSTRAP_ADDRESS="localhost"
COMMON_ARGS="--BOOTSTRAP_PORT=10001 --BOOTSTRAP_TOMP2P_PORT=12001"

if [ $1 ]; then
  PEERNAME=$1
  TESTNAME=$2
  PEER_NUM=$3
  TEST_CLIENTS_NUM=$4
  TEST_ITER=$5
  BOOTSTRAP_ADDRESS=$6
fi

# Generate test list.
for i in `seq 1 $TEST_ITER`
do
    if [ $i -ne 1 ]
    then
        CONCAT=$CONCAT;
    fi
    CONCAT=$CONCAT$TESTNAME
done

# Configure peers.
for i in `seq 1 $PEER_NUM`
do
    # Make first peer bootstrap server, storage holder and test server.
    if [ $i -eq 1 ]
    then
        BOOTSTRAP_MODE="server"
        BDB_TYPE="storage-holder"
        IS_SERVER="true"
    else
        BOOTSTRAP_MODE="client"
        BDB_TYPE="proxy"
        IS_SERVER="false"
    fi

    PADDED=`printf "%03d" $i`
    ./resources/conf/generate_config.py $COMMON_ARGS --APP_KEY=$i$i --CLASS_NAME=$PEERNAME --TEST_LIST=$CONCAT\
         --BOOTSTRAP_MODE=$BOOTSTRAP_MODE --CLI_PORT=11$PADDED --TOMP2P_PORT=12$PADDED --BDB_TYPE=$BDB_TYPE\
         --BOOTSTRAP_ADDRESS=$BOOTSTRAP_ADDRESS --IS_SERVER=$IS_SERVER --NUM_TEST_PARTICIPANTS=$TEST_CLIENTS_NUM\
         --COMM_ADDRESS=00000000-0000-0000-0$PADDED-000000000000 < ./resources/conf/Peer.xml.template > Peer.xml.$i
done

