#!/bin/bash

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

LOG_FOLDER=$(date +%Y%m%d_%H%M%S)

echo "Copying logs to: $LOG_FOLDER"
mkdir $LOG_FOLDER
cp -r ${TEST_NAME}/*.log ${LOG_FOLDER}/

RESULT=$1
shift

echo -e "Test ended with: $RESULT exit code \n. Test run with args: $@" >\
    ${LOG_FOLDER}/RESULT
