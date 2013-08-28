#!/bin/bash

# Generate config files for test peers. First peer is bootstrap server.
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
COMMON_ARGS="--bootstrap-port=10001 --bootstrap-server-tomp2p-port=12001"
DATA_FILE=test.data

if [ $1 ]; then
  PEERNAME=$1
  PEERNAME_CONFIGURATION=$2
  TESTNAME=$3
  PEER_NUM=$4
  TEST_CLIENTS_NUM=$5
  TEST_ITER=$6
  BOOTSTRAP_ADDRESS=$7
  DATA_FILE=${8-''}
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
    ./resources/conf/generate_config.py $COMMON_ARGS --app-key=$i$i --class-name=$PEERNAME\
         --configuration-class-name=$PEERNAME_CONFIGURATION --testing-peer-class-list=$CONCAT\
         --bootstrap/mode=$BOOTSTRAP_MODE --comm-cli-port=11$PADDED --tomp2p-port=12$PADDED\
         --bdb-peer/type=$BDB_TYPE\
         --bootstrap/address=$BOOTSTRAP_ADDRESS --systest/is-server=$IS_SERVER\
         --num-test-participants=$TEST_CLIENTS_NUM\
         --systest/data-file=$DATA_FILE\
         --comm-address=00000000-0000-0000-0$PADDED-000000000000 < ./resources/conf/Peer.xml.template > Peer.xml.$i
done

