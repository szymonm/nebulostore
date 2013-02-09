#!/bin/bash

# Who is running the test
# Change it to valid value present in build-location.txt to easily change build
# directory
LOCAL_USER=grzesiek

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

BUILD_LOCATION_FILE=build-location.txt
BUILD_DIR=$(grep "^$LOCAL_USER" $BUILD_LOCATION_FILE | cut -f2 -d:)
BUILD_DIR=$BUILD_DIR

echo "Starting copying"

mkdir --parents build
rsync -r --cvs-exclude $BUILD_DIR/target/nebulostore-0.5.jar ./build
rsync -r --cvs-exclude $BUILD_DIR/target/lib ./build
rsync -r --cvs-exclude $BUILD_DIR/resources ./build

cd build/resources/conf
./generate_config.py --APP_KEY=1 --CLASS_NAME=$TEST_NAME \
--NUM_GOSSIPERS=100 --GOSSIP_PERIOD=2000 --MAX_PEERS_SIZE=10 \
--HEALING_FACTOR=1 --SWAPPING_FACTOR=1  < Peer.xml.template > Peer.xml

echo "Copying done"
