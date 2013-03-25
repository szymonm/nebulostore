#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_iterations
# Prints "SUCCESS" or "FAILURE"

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_ITER=3
BOOTSTRAP_DELAY=3
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
./scripts/generate-config-files.sh $PEERNAME $TESTNAME $PEER_NUM $((PEER_NUM-1)) $TEST_ITER localhost
for i in `seq 1 $PEER_NUM`
do
    mv Peer.xml.$i ./build/jar/$i/resources/conf/Peer.xml
done

# Run server normally and clients in background.
echo "RUNNING ..."
cd build/jar

run_clients() {
    sleep $BOOTSTRAP_DELAY
    for i in `seq 2 $PEER_NUM`
    do
        cd $i
        java -jar Nebulostore.jar &
        cd ..
    done
}
run_clients &

EXIT_CODE=0
cd 1
java -jar Nebulostore.jar
if [ $? -ne 0 ]; then
  EXIT_CODE=1
fi
cd ../../..

# Copy logs.
rm -rf $LOG_DIR
mkdir $LOG_DIR
echo "COPYING LOGS ..."
for i in `seq 1 $PEER_NUM`
do
    cp -r build/jar/$i/logs $LOG_DIR/logs_$i
done

# Kill remaining peers.
kill `ps -ao pid,command | grep Nebulostore | grep -v grep | awk '{ print $1 }'`
if [ $EXIT_CODE -eq 0 ]
then
    echo "SUCCESS"
else
    echo "FAILURE"
fi
exit $EXIT_CODE
