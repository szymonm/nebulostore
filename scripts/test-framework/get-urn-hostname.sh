#!/bin/bash
#usage : logs_dir

LOG_DIR=$1

find $LOG_DIR -iname "comm.log" | xargs head -n 9 | grep -v "\- PeerID" | grep "urn\|log" 
