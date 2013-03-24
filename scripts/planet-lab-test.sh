#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_iterations
# Prints "SUCCESS" or "FAILURE"

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_ITER=3
HOST_LIST="scripts/hosts.txt"

BOOTSTRAP_PEER="localhost"
BOOTSTRAP_DELAY=5

USER=mimuw_nebulostore
REMOTE_DIR=`whoami`
LOG_DIR=logs
SSH_OPTIONS="StrictHostKeyChecking=no"

if [ $1 ]; then
  PEERNAME=$1
  TESTNAME=$2
  PEER_NUM=$3
  TEST_ITER=$4
  HOST_LIST=$5
fi

# Build and copy peers.
echo "BUILDING ..."
./scripts/build-and-deploy.sh 1 peer > /dev/null
echo "COPYING ..."
i=1
for host in `cat $HOST_LIST`
do
    if [ $i -eq 1 ]
    then
        BOOTSTRAP_PEER=$host
    fi
    echo "  " $host
    ssh -o $SSH_OPTIONS -l $USER $host "rm -rf $REMOTE_DIR/logs* $REMOTE_DIR/storage* $REMOTE_DIR/resources* $REMOTE_DIR/Nebulostore.jar"
    rsync -ru --size-only ./build/jar/1/* $USER@$host:~/$REMOTE_DIR/
    if [ $i -eq $PEER_NUM ]
    then
        break
    else
        ((i++))
    fi
done

# Generate and copy config files.
echo "RUNNING ..."
./scripts/generate-config-files.sh $PEERNAME $TESTNAME $PEER_NUM $TEST_ITER $BOOTSTRAP_PEER
i=1
for host in `cat $HOST_LIST`
do
    echo "  " $host
    rsync Peer.xml.$i $USER@$host:~/$REMOTE_DIR/resources/conf/Peer.xml
    rm Peer.xml.$i
    if [ $i -eq $PEER_NUM ]
    then
        ssh -o $SSH_OPTIONS -l $USER $host "cd $REMOTE_DIR; java -jar Nebulostore.jar > logs/stdout.log 2> logs/stderr.log && echo 'OK' > logs/exit_code || echo 'ERROR' > logs/exit_code"
        break
    else
        ssh -o $SSH_OPTIONS -l $USER $host "cd $REMOTE_DIR; java -jar Nebulostore.jar > logs/stdout.log 2> logs/stderr.log &"
        if [ $i -eq 1 ]
        then
            sleep 2
        fi
        ((i++))
    fi
done

rm -rf $LOG_DIR
mkdir $LOG_DIR
# Kill peers and collect logs.
echo "KILLING PEERS AND COLLECTING LOGS ..."
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
cat $LOG_DIR/logs_${PEER_NUM}/exit_code
