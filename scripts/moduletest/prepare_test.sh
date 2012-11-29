#!/bin/bash

# Who is running the test
# Change it to valid value present in build-location.txt to easily change build
# directory
LOCAL_USER=grzesiek

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

BUILD_LOCATION_FILE=build-location.txt
BUILD_DIR=$(grep "^$LOCAL_USER" $BUILD_LOCATION_FILE | cut -f2 -d:)
BUILD_DIR=$BUILD_DIR/$TEST_NAME

echo "Starting copying"

rm -rf $TEST_NAME

rsync -r --cvs-exclude $BUILD_DIR ./

echo "Copying done"
