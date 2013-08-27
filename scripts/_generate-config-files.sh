#!/bin/bash

# Generate config files for test peers.
# Uses config template from ./resources/conf/Peer.xml.template updated
# with parameters from CONFIG_TEMPLATE_UPDATE XML file (if set).
#
# First peer is bootstrap server.
# Last peer is testing peer.
# Optionally, the following parameters might be provided:
#   peer_class_name test_class_name number_of_peers number_of_test_clients number_of_test_iterations
#   bootstrap_address data_file

PEERNAME="org.nebulostore.systest.TestingPeer"
PEERNAME_CONFIGURATION="org.nebulostore.systest.TestingPeerConfiguration"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_CLIENTS_NUM=2
TEST_ITER=3
BOOTSTRAP_ADDRESS="localhost"
COMMON_ARGS="--BOOTSTRAP_PORT=10001 --BOOTSTRAP_TOMP2P_PORT=12001"
DATA_FILE=test.data
CONFIG_TEMPLATE_UPDATE=""

if [ $1 ]; then
  PEERNAME=$1
  PEERNAME_CONFIGURATION=$2
  TESTNAME=$3
  PEER_NUM=$4
  TEST_CLIENTS_NUM=$5
  TEST_ITER=$6
  BOOTSTRAP_ADDRESS=$7
  DATA_FILE=${8-''}
  CONFIG_TEMPLATE_UPDATE=$9
fi


# Generate test list.
for ((i=1; i<=$TEST_ITER; i++))
do
    if [ $i -ne 1 ]
    then
        CONCAT=$CONCAT;
    fi
    CONCAT=$CONCAT$TESTNAME
done

if [ $9 ]; then
  ./scripts/_update-xml.sh resources/conf/Peer.xml.template $CONFIG_TEMPLATE_UPDATE > Peer.xml.base
else
  cp resources/conf/Peer.xml.template Peer.xml.base
fi

# Configure peers.
for ((i=1; i<=$PEER_NUM; i++))
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
    ./resources/conf/generate_config.py $COMMON_ARGS --APP_KEY=$i$i --CLASS_NAME=$PEERNAME\
         --CONFIGURATION_CLASS_NAME=$PEERNAME_CONFIGURATION --TEST_LIST=$CONCAT\
         --BOOTSTRAP_MODE=$BOOTSTRAP_MODE --CLI_PORT=11$PADDED --TOMP2P_PORT=12$PADDED --BDB_TYPE=$BDB_TYPE\
         --BOOTSTRAP_ADDRESS=$BOOTSTRAP_ADDRESS --IS_SERVER=$IS_SERVER --NUM_TEST_PARTICIPANTS=$TEST_CLIENTS_NUM\
         --DATA_FILE=$DATA_FILE\
         --COMM_ADDRESS=00000000-0000-0000-0$PADDED-000000000000 < Peer.xml.base > Peer.xml.$i
done

