#!/bin/bash

. init_util.sh

bad_servers=""

function my_ping {
    echo "Pinging $1"
    ping -c 4 $1 || bad_servers="$bad_servers\n$1"
}

my_ping $BOOTSTRAP_SERVER
my_ping $PRIMARY_HOST

for HOST in $SLICE_HOSTS; do
    my_ping $HOST
done

echo "bad_servers:"
echo -e $bad_servers
