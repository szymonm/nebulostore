#!/bin/bash

./scripts/planet-lab-test.sh\
        org.nebulostore.systest.TestingPeer\
        org.nebulostore.systest.readwrite.ReadWriteServer\
        8 6 1\
        scripts/hosts.txt
