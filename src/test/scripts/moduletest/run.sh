#!/bin/bash
#Author: Grzegorz Milka

TEST_NAME_FILE=test_name.txt
TEST_NAME=`cat $TEST_NAME_FILE`

echo "Running $TEST_NAME"
cd $TEST_NAME
java -Xmx450m -Xss2M -jar Nebulostore.jar "$@" > stdout.log 2> stderr.log
