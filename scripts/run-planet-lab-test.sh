#!/bin/bash

if [ $1 ]; then
    N=$1
else
    echo "Please select PlanetLab test to run (or provide the number as a parameter):"
    echo "    1) ping-pong test (3 peers / 2 test clients)"
    echo "    2) read-write test (7 peers / 5 test clients)"
    echo "    3) lists test (7 peers / 5 test clients)"
    echo "    4) performance lists test (11 peers / 8 test clients)"
    echo "    5) performance lists test (25 peers / 20 test clients)"
    echo "    6) performance lists test (35 peers / 30 test clients)"
    echo "    7) performance lists test (60 peers / 50 test clients)"
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
           11 8 1\
           scripts/hosts.txt;;
    5) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           25 20 1\
           scripts/hosts.txt;;
    6) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           35 30 1\
           scripts/hosts.txt;;
    7) ./scripts/planet-lab-test.sh\
           org.nebulostore.systest.performance.PerfTestingPeer\
           org.nebulostore.systest.lists.ListsServer\
           50 40 1\
           scripts/hosts.txt;;
esac

