#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
#
# Make sure that you have uploaded necessary libraries to the host
# Use scripts/upload-libs-to-planet-lab.sh for that purpose.
#
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_test_clients number_of_iterations
# Prints "SUCCESS" or "FAILURE"

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_CLIENTS_NUM=2
TEST_ITER=3
HOST_LIST="scripts/hosts.txt"

BOOTSTRAP_PEER="localhost"
BOOTSTRAP_DELAY=5

USER=mimuw_nebulostore
REMOTE_DIR="nebulostore_autodeploy/"`whoami`
LOG_DIR=logs
SSH_OPTIONS="StrictHostKeyChecking=no"

if [ $1 ]; then
  PEERNAME=$1
  TESTNAME=$2
  PEER_NUM=$3
  TEST_CLIENTS_NUM=$4
  TEST_ITER=$5
  HOST_LIST=$6
fi

BOOTSTRAP_PEER=`cat $HOST_LIST | head -n 1`

echo "BUILDING ..."
./scripts/build-and-deploy.sh 1 peer > /dev/null
./scripts/generate-config-files.sh $PEERNAME $TESTNAME $PEER_NUM $TEST_CLIENTS_NUM $TEST_ITER $BOOTSTRAP_PEER

echo "COPYING ..."
i=1
for host in `cat $HOST_LIST`
do
    echo "  " $host
    ssh -o $SSH_OPTIONS -l $USER $host "mkdir -p $REMOTE_DIR; rm -rf $REMOTE_DIR/logs* $REMOTE_DIR/storage* $REMOTE_DIR/resources*"
    mv Peer.xml.$i build/jar/1/resources/conf/Peer.xml
    rsync -rul ./build/jar/1/* $USER@$host:~/$REMOTE_DIR/
    if [ $i -eq $PEER_NUM ]
    then
        break
    else
        ((i++))
    fi
done

echo "RUNNING ..."

run_clients() {
    sleep $BOOTSTRAP_DELAY
    i=1
    for host in `cat $HOST_LIST`
    do
        if [ $i -ne 1 ]
        then
            echo "  " $host
            ssh -o $SSH_OPTIONS -l $USER $host "cd $REMOTE_DIR; java -jar Nebulostore.jar > logs/stdout.log 2> logs/stderr.log &"
        fi
        if [ $i -eq $PEER_NUM ]
        then
            break
        else
            ((i++))
        fi
    done
}

run_clients &
echo "  " $BOOTSTRAP_PEER
ssh -o $SSH_OPTIONS -l $USER $BOOTSTRAP_PEER "cd $REMOTE_DIR; java -jar Nebulostore.jar > logs/stdout.log 2> logs/stderr.log && echo 'OK' > logs/exit_code || echo 'ERROR' > logs/exit_code"

echo "KILLING PEERS AND COLLECTING LOGS ..."
rm -rf $LOG_DIR
mkdir $LOG_DIR
i=1
for host in `cat $HOST_LIST`
do
    echo "  " $host
    ssh -o $SSH_OPTIONS -l $USER $host "cd $REMOTE_DIR; killall java &> /dev/null; tar -zcf logs.tar.gz logs"
    rsync $USER@$host:~/$REMOTE_DIR/logs.tar.gz $LOG_DIR/logs_${i}_${host}.tar.gz
    cd $LOG_DIR
    tar -xzf logs_${i}_${host}.tar.gz
    mv logs logs_$i
    cd ..
    if [ $i -eq $PEER_NUM ]
    then
        break
    else
        ((i++))
    fi
done

echo -n "TEST RESULT: "
cat $LOG_DIR/logs_1/exit_code
