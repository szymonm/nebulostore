#!/bin/bash

# Automatic local 3-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: TestServerClassName number_of_peers number_of_iterations

PEER_NUM=3
BOOTSTRAP_DELAY=2
TEST_ITER=3

PEERNAME="org\.nebulostore\.appcore\.Peer"
TESTPEERNAME="org\.nebulostore\.conductor\.TestingPeer"
TESTNAME="org\.nebulostore\.conductor\.pingpong\.PingPongServer"

if [ $1 ]; then
  TESTNAME=$1
  PEER_NUM=$2
  TEST_ITER=$3
fi

# Build peers using console-test script.
echo "BUILDING ..."
./scripts/console-test.sh $PEER_NUM peer > /dev/null

# Configure peers.
for i in `seq 1 $(($PEER_NUM-1))`
do
    sed -e "s/__APP_KEY/$i$i/g" -e "s/__CLASS_NAME/$PEERNAME/g" ./resources/conf/Peer.xml.template > ./build/jar/$i/resources/conf/Peer.xml
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
#echo $CONCAT
#echo $TESTNAME
#echo $PEER_NUM
#echo $TEST_ITER
sed -e "s/__APP_KEY/$PEER_NUM$PEER_NUM/g" -e "s/__CLASS_NAME/$TESTPEERNAME/g" -e "s/__TESTS_LIST/$CONCAT/g" ./resources/conf/Peer.xml.template > ./build/jar/$PEER_NUM/resources/conf/Peer.xml

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
java -jar Nebulostore.jar
if [ $? -eq 0 ]; then
  echo "SUCCESS"
else
  echo "FAILURE"
fi

# Kill remaining peers.
kill `jobs -p`
