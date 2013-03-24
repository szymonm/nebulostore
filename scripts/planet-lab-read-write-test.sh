#!/bin/bash

./scripts/planet-lab-test.sh\
        org.nebulostore.systest.TestingPeer\
        org.nebulostore.systest.readwrite.ReadWriteServer\
        6 1\
        scripts/hosts.txt
