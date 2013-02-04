#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: TestServerClassName number_of_peers number_of_iterations
# Prints "SUCCESS" or "FAILURE"

TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_ITER=3
BOOTSTRAP_DELAY=2

if [ $1 ]; then
  TESTNAME=$1
  PEER_NUM=$2
  TEST_ITER=$3
fi

# Build peers.
echo "BUILDING ..."
./scripts/build-and-deploy.sh $PEER_NUM peer > /dev/null

# Generate and copy config files.
./scripts/generate-config-files.sh $TESTNAME $PEER_NUM $TEST_ITER localhost
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
if [ $? -eq 0 ]; then
  echo "SUCCESS"
else
  echo "FAILURE"
  EXIT_CODE=1
fi

# Kill remaining peers.
kill `jobs -p`
exit $EXIT_CODE
