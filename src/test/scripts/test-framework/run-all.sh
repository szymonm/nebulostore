#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
REN_HOSTS_FILE=rendez-hosts.txt


REMOTE_DIR=`whoami`


DELAY=$1

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_BUILD_DIR=`cat $BUILD_PRIMARY_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
REN_HOSTS=`cat $REN_HOSTS_FILE`

APP_KEY=1

sleep 2


echo "Running jar on $PRIMARY_HOST with appKey $APP_KEY"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &"
((APP_KEY++))


sleep 5


for HOST in $REN_HOSTS; do
    echo "Running jar on rendezvous host with $HOST appKey $APP_KEY"
    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &" 
    sleep 10

    ((APP_KEY++))
done

sleep 10


for HOST in $SLICE_HOSTS; do
    echo "Running jar on $HOST with appKey $APP_KEY"
    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &" 
    sleep 20
    ((APP_KEY++))
done


sleep 5

