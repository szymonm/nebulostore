#!/bin/bash
# usage: delay_in_sec

# script is assuming your private key name is planet-lab
# it works for user mimuw_nebulostore

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 {DELAY}"
    exit 1;
fi

DELAY=$1

# -1. Cleaning previous logs
bash clean-logs.sh

# 0. copying data to remote hosts
bash prepare-test.sh

# 1. running jar on all hosts
bash run-all.sh

# 2. sleeping...
echo "Sleeping for $DELAY..."
sleep $DELAY
echo "Waken up"

# 3. killing instances on remote hosts
bash kill-all.sh
