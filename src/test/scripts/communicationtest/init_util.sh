#!/bin/bash
# Author and maintainer: Grzegorz Milka
# Initializes common variables and declares helper functions

# Normal peers
export SLICE_HOSTS_FILE=slice-hosts.txt
export SLICE_HOSTS=$(cat $SLICE_HOSTS_FILE)
# Peer running testing server
export PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
export PRIMARY_HOST=$(cat $PRIMARY_SLICE_HOST_FILE)
# Peer running as gossip and tomp2p bootstrap
export BOOTSTRAP_SERVER_FILE=bootstrap-server.txt
export BOOTSTRAP_SERVER=$(cat $BOOTSTRAP_SERVER_FILE)

# name of dir to put my files with tests
export REMOTE_DIR=$(cat local_user.txt)
# login to planetlab-hosts
export USER=$(cat ssh-user.txt)

# return all hosts participating in test
function get_all_hosts() {
    echo $BOOTSTRAP_SERVER
    echo $PRIMARY_HOST
    for HOST in $SLICE_HOSTS; do echo $HOST; done
}
