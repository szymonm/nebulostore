#!/bin/bash

SCRIPT_NAME=./_local-test.sh
N_TESTS=6
declare -a PEERS=(3 6 6 8 14 6)
declare -a TITLES=(\
    'ping-pong test'\
    'read-write test'\
    'lists test'\
    'performance lists test'\
    'performance lists test'\
    'read-write time measure test'\
    )

EXEC_DIR=$(pwd)
cd $(dirname $0)


if [ $1 ]; then
    N=$1
else
    echo "Please select local test to run (or provide the number as a parameter):"
    echo "    0) All"
    for ((i=1; i<=N_TESTS; ++i))
    do
        echo "    $i) "${TITLES[$((i-1))]}" (${PEERS[$((i-1))]} peers)"
    done
    read N
fi

case $N in
    0) for ((i=1; i<=5; ++i)); do echo "*** Test $i - ${TITLES[$((i-1))]}"; $0 $i; done;;
    1) $SCRIPT_NAME\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.TestingPeerConfiguration\
           org.nebulostore.systest.pingpong.PingPongServer\
           ${PEERS[0]} 1;;
    2) $SCRIPT_NAME\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.TestingPeerConfiguration\
           org.nebulostore.systest.readwrite.ReadWriteServer\
           ${PEERS[1]} 1;;
    3) $SCRIPT_NAME\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.TestingPeerConfiguration\
           org.nebulostore.systest.lists.ListsServer\
           ${PEERS[2]} 1;;
    4) $SCRIPT_NAME\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.performance.PerfTestingPeerConfiguration\
           org.nebulostore.systest.lists.ListsServer\
           ${PEERS[3]} 1;;
    5) $SCRIPT_NAME\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.performance.PerfTestingPeerConfiguration\
           org.nebulostore.systest.lists.ListsServer\
           ${PEERS[4]} 1;;
    6) $SCRIPT_NAME\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.readwrite.ReadWriteWithTimeConfiguration\
           org.nebulostore.systest.readwrite.ReadWriteServer\
           ${PEERS[1]} 1 ../../../test.data;;

esac

cd ${EXEC_DIR}
