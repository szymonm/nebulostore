#!/bin/bash

# Generate config files for test peers. First peer is bootstrap server.
# Last peer is testing peer.
# Optionally, the following parameters might be provided:
#   -p peer_class_name
#   -c configuration_class_name
#   -t test_class_name
#   -n number_of_peers
#   -m number_of_test_clients
#   -i number_of_test_iterations
#   -b bootstrap_address
#   -d [data_file]

EXEC_DIR=$(pwd)
cd $(dirname $0)

while getopts ":p:c:t:n:m:i:b:d:" OPTION
do
  case $OPTION in
    p) PEERNAME=$OPTARG;;
    c) PEERNAME_CONFIGURATION=$OPTARG;;
    t) TESTNAME=$OPTARG;;
    n) PEER_NUM=$OPTARG;;
    m) TEST_CLIENTS_NUM=$OPTARG;;
    i) TEST_ITER=$OPTARG;;
    b) BOOTSTRAP_ADDRESS=$OPTARG;;
    d) DATA_FILE=$OPTARG;;
    # DEFAULT
    *)
       ARG=$(($OPTIND-1));
       if [ ${!ARG} = "-d" ]
       then
           DATA_FILE=""
       else
           echo "Unknown option option chosen: ${!ARG}.";
       fi
  esac
done

: ${PEERNAME="org.nebulostore.systest.TestingPeer"}
: ${PEERNAME_CONFIGURATION="org.nebulostore.systest.TestingPeerConfiguration"}
: ${TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"}
: ${PEER_NUM=3}
: ${TEST_CLIENTS_NUM=2}
: ${TEST_ITER=3}
: ${BOOTSTRAP_ADDRESS="localhost"}
: ${COMMON_ARGS="--bootstrap-port=10001 --bootstrap-server-tomp2p-port=12001"}
: ${DATA_FILE=test.data}

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
    ../resources/conf/generate_config.py $COMMON_ARGS --app-key=$i$i --class-name=$PEERNAME\
         --configuration-class-name=$PEERNAME_CONFIGURATION --testing-peer-class-list=$CONCAT\
         --bootstrap/mode=$BOOTSTRAP_MODE --comm-cli-port=11$PADDED --tomp2p-port=12$PADDED\
         --bdb-peer/type=$BDB_TYPE\
         --bootstrap/address=$BOOTSTRAP_ADDRESS --systest/is-server=$IS_SERVER\
         --num-test-participants=$TEST_CLIENTS_NUM\
         --systest/data-file=$DATA_FILE\
         --comm-address=00000000-0000-0000-0$PADDED-000000000000\
         --bdb-peer/holder-comm-address=00000000-0000-0000-0001-000000000000 < ../resources/conf/Peer.xml.template > ../Peer.xml.$i
done

cd ${EXEC_DIR}
