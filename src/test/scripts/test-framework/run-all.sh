#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
BUILD_BOOTSTRAP_LOCATION_FILE=bootstrap-server-build-location.txt
KEY_LOCATION_FILE=key-location.txt
SLICE_HOSTS_FILE=slice-hosts.txt
PRIMARY_SLICE_HOST_FILE=primary-slice-host.txt
BOOTSTRAP_SERVER_FILE=bootstrap-server.txt

REMOTE_DIR=`whoami`

DELAY=$1

BUILD_DIR=`cat $BUILD_LOCATION_FILE`
SLICE_HOSTS=`cat $SLICE_HOSTS_FILE`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`
PRIMARY_BUILD_DIR=`cat $BUILD_PRIMARY_LOCATION_FILE`
BOOTSTRAP_BUILD_DIR=`cat $BUILD_BOOTSTRAP_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
BOOTSTRAP_SERVER=`cat $BOOTSTRAP_SERVER_FILE`
BDB_HOLDER="host1.planetlab.informatik.tu-darmstadt.de"

APP_KEY=1

echo "Running BootstrapServer jar on $BOOTSTRAP_SERVER"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $BOOTSTRAP_SERVER "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar > std.out.log 2> std.err.log &"

sleep 1

echo "Running BDBHolder jar on $BDB_HOLDER"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $BDB_HOLDER "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &"
((APP_KEY++))

echo "Running jar on $PRIMARY_HOST with appKey $APP_KEY"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &"
((APP_KEY++))


sleep 5

for HOST in $SLICE_HOSTS; do
    echo "Running jar on $HOST with appKey $APP_KEY"

    ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY > std.out.log 2> std.err.log &" 
    if [ $APP_KEY -gt 17 ]; then
        sleep 300
    fi
    sleep 1
    ((APP_KEY++))
done


sleep 2

