#!/bin/bash

if [ $1 ]; then
    N=$1
else
    echo "Please select PlanetLab test to run (or provide the number as a parameter):"
    echo "    1) ping-pong test (3 peers)"
    echo "    2) read-write test (7 peers)"
    echo "    3) lists test (7 peers)"
    echo "    4) performance lists test (10 peers)"
    echo "    5) performance lists test (15 peers)"
    echo "    6) performance lists test (30 peers)"
    read N
fi

case $N in
    1) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.pingpong.PingPongServer\
           3 2 1\
           scripts/hosts.txt;;
    2) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.readwrite.ReadWriteServer\
           7 5 1\
           scripts/hosts.txt;;
    3) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.TestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           7 5 1\
           scripts/hosts.txt;;
    4) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           10 8 1\
           scripts/hosts.txt;;
    5) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           15 12 1\
           scripts/hosts.txt;;
    6) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           30 24 1\
           scripts/hosts.txt;;
esac

