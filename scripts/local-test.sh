#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_iterations
# Prints "SUCCESS" or "FAILURE"

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_ITER=3
BOOTSTRAP_DELAY=2
LOG_DIR=logs

if [ $1 ]; then
  PEERNAME=$1
  TESTNAME=$2
  PEER_NUM=$3
  TEST_ITER=$4
fi

# Build peers.
echo "BUILDING ..."
./scripts/build-and-deploy.sh $PEER_NUM peer > /dev/null

# Generate and copy config files.
./scripts/generate-config-files.sh $PEERNAME $TESTNAME $PEER_NUM $TEST_ITER localhost
for i in `seq 1 $PEER_NUM`
do
    mv Peer.xml.$i ./build/jar/$i/resources/conf/Peer.xml
done

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
if [ $? -ne 0 ]; then
  EXIT_CODE=1
fi
cd ../../..

rm -rf $LOG_DIR
mkdir $LOG_DIR
echo "COPYING LOGS ..."
for i in `seq 1 $PEER_NUM`
do
    cp -r build/jar/$i/logs $LOG_DIR/logs_$i
done

# Kill remaining peers.
kill `jobs -p`
if [ $EXIT_CODE -eq 0 ]
then
    echo "SUCCESS"
else
    echo "FAILURE"
fi
exit $EXIT_CODE
