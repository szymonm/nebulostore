#!/bin/bash

./scripts/planet-lab-test.sh\
        org.nebulostore.systest.TestingPeer\
        org.nebulostore.systest.pingpong.PingPongServer\
        3 2 3\
        scripts/hosts.txt
