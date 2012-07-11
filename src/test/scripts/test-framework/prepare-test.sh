#!/bin/bash

BUILD_LOCATION_FILE=build-location.txt
BUILD_PRIMARY_LOCATION_FILE=primary-build-location.txt
BUILD_BOOTSTRAP_LOCATION_FILE=bootstrapserver-build-location.txt
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
BOOTSTRAP_BUILD_DIR=`cat $BUILD_BOOTSTRAP_LOCATION_FILE`
PRIMARY_HOST=`cat $PRIMARY_SLICE_HOST_FILE`
USER=`cat ssh-user.txt`
REN_HOSTS=`cat $REN_HOSTS_FILE`


BDB_HOLDER="host1.planetlab.informatik.tu-darmstadt.de"

#echo "Cleaning temporary files on $BDB_HOLDER and $PRIMARY_HOST"
#ssh -i $KEY_LOCATION/planetlab-key -l $USER $BDB_HOLDER "rm -rf /tmp/*"
echo "Cleaning temporary files on $PRIMARY_HOST"
ssh -i $KEY_LOCATION/planetlab-key -l $USER $PRIMARY_HOST "rm -rf /tmp/*"


echo "Copying data on remote hosts..."
HOST_NO=1
ALL_HOSTS=`cat $SLICE_HOSTS_FILE | wc -l`


IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Copying to $HOST ($HOST_NO/$ALL_HOSTS)"
    RDV=`expr $HOST_NO % 1`
    bash scp.sh $HOST $BUILD_DIR $KEY_LOCATION $REMOTE_DIR && ssh -i $KEY_LOCATION/planetlab-key -l $USER $HOST "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication; mv JxtaPeer_$RDV.xml JxtaPeer.xml"
    ((HOST_NO++))
done

echo "Copying to rendezvous hosts"


for HOST in $REN_HOSTS; do
    echo "Copying to $HOST"
    bash scp.sh $HOST $BOOTSTRAP_BUILD_DIR $KEY_LOCATION $REMOTE_DIR 
done

sleep 5
echo "Copying to $PRIMARY_HOST"

bash scp.sh host2.planetlab.informatik.tu-darmstadt.de $PRIMARY_BUILD_DIR $KEY_LOCATION $REMOTE_DIR 
ssh -i $KEY_LOCATION/planetlab-key -l $USER host2.planetlab.informatik.tu-darmstadt.de "ulimit -u 2000; cd $REMOTE_DIR/resources/conf/communication; mv JxtaPeer_0.xml JxtaPeer.xml" &

#echo "Configuring BDB DHT holder at $BDB_HOLDER"
#ssh -i $KEY_LOCATION/planetlab-key -l $USER $BDB_HOLDER "cd $REMOTE_DIR; cp ./resources/conf/communication/BdbPeer_holder.xml ./resources/conf/communication/BdbPeer.xml"


echo "Copying done"

