#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: TestServerClassName number_of_peers number_of_iterations
# Prints "SUCCESS" or "FAILURE"

PEER_NUM=3
BOOTSTRAP_DELAY=2
TEST_ITER=3

PEERNAME="org.nebulostore.appcore.Peer"
TESTPEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"

COMMON_ARGS="--BOOTSTRAP_ADDRESS=localhost --BOOTSTRAP_TOMP2P_PORT=10301"

if [ $1 ]; then
  TESTNAME=$1
  PEER_NUM=$2
  TEST_ITER=$3
fi

# Build peers.
echo "BUILDING ..."
./scripts/build-and-deploy.sh $PEER_NUM peer > /dev/null

# Configure peers.
for i in `seq 1 $(($PEER_NUM-1))`
do
    if [ $i -eq 1 ]
    then
        BOOTSTRAP_MODE="server"
        BDB_TYPE="storage-holder"
    else
        BOOTSTRAP_MODE="client"
        BDB_TYPE="proxy"
    fi
    ./resources/conf/generate_config.py $COMMON_ARGS --APP_KEY=$i$i --CLASS_NAME=$PEERNAME --BOOTSTRAP_MODE=$BOOTSTRAP_MODE\
         --CLI_PORT=1010$i --BOOTSTRAP_PORT=10201 --TOMP2P_PORT=1030$i --BDB_TYPE=$BDB_TYPE\
         < ./resources/conf/Peer.xml.template > ./build/jar/$i/resources/conf/Peer.xml
done

# Configure last peer as test server.
for i in `seq 1 $TEST_ITER`
do
    if [ $i -ne 1 ]
    then
        CONCAT=$CONCAT,
    fi
    CONCAT=$CONCAT$TESTNAME
done

./resources/conf/generate_config.py $COMMON_ARGS --APP_KEY=$PEER_NUM$PEER_NUM --CLASS_NAME=$TESTPEERNAME --TEST_LIST=$CONCAT\
    --BOOTSTRAP_MODE=client --CLI_PORT=1010$PEER_NUM --BOOTSTRAP_PORT=10201 --TOMP2P_PORT=1030$PEER_NUM\
    --BDB_TYPE=proxy < ./resources/conf/Peer.xml.template > ./build/jar/$PEER_NUM/resources/conf/Peer.xml

# Run peers.
echo "RUNNING ..."
cd build/jar
for i in `seq 1 $(($PEER_NUM-1))`
do
    cd $i
    java -jar Nebulostore.jar &
    sleep $BOOTSTRAP_DELAY
    cd ..
done

cd $PEER_NUM
EXIT_CODE=0
java -jar Nebulostore.jar
if [ $? -eq 0 ]; then
  echo "SUCCESS"
else
  echo "FAILURE"
  EXIT_CODE=1
fi

# Kill remaining peers.
kill `jobs -p`
exit $EXIT_CODE
