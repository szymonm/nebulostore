#!/bin/bash

. init_util.sh

echo "Killing all instances on remote hosts"

for HOST in $SLICE_HOSTS; do
    echo "Killing instance running on $HOST"
    ssh -o ConnectTimeout=5 -l mimuw_nebulostore $HOST 'killall java'
    ssh -o ConnectTimeout=5 -l mimuw_nebulostore $HOST "rm -rf $REMOTE_DIR/.jxta"
done

echo "Killing instance running on $PRIMARY_HOST"
ssh -o ConnectTimeout=5 -l mimuw_nebulostore $PRIMARY_HOST 'killall java'

echo "Killing BootstrapServer running on $BOOTSTRAP_SERVER"
ssh -o ConnectTimeout=5 -l mimuw_nebulostore $BOOTSTRAP_SERVER 'killall java'
