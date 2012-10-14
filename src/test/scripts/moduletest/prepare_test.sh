#!/bin/bash

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

BUILD_LOCATION_FILE=build-location.txt
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

echo "Starting copying"

rm -rf $TEST_NAME

rsync -r --cvs-exclude $BUILD_DIR ./

echo "Copying done"
