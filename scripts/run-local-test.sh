#!/bin/bash

if [ $1 ]; then
    N=$1
else
    echo "Please select local test to run (or provide the number as a parameter):"
    echo "    1) ping-pong test (3 peers)"
    echo "    2) read-write test (6 peers)"
    echo "    3) lists test (6 peers)"
    echo "    4) performance lists test (8 peers)"
    echo "    5) performance lists test (14 peers)"
    read N
fi

case $N in
    1) ./scripts/local-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.pingpong.PingPongServer\
           3 1;;
    2) ./scripts/local-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.readwrite.ReadWriteServer\
           6 1;;
    3) ./scripts/local-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           6 1;;
    4) ./scripts/local-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           8 1;;
    5) ./scripts/local-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           14 1;;
esac

