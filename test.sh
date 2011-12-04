#!/bin/bash

JAR_DIR="build/jar"
JAR="Nebulostore.jar"
PEERS_NUM=3
TEST_RUN_TIME=200

rm -rf ./jar/*

ant jar-pingpong


echo "Building done. Copying..."

for i in `seq 1 $PEERS_NUM`
do
    path="./$JAR_DIR/$i"
    rm -rf $path
    mkdir $path
    cp ./$JAR_DIR/*.jar ./$JAR_DIR/$i/
    cp -r ./$JAR_DIR/lib ./$JAR_DIR/$i/
    cp ./log4j.xml ./$JAR_DIR/$i/
done

echo "Copying done. Running..."

for i in `seq 1 $PEERS_NUM`
do
    cd ./$JAR_DIR/$i
    java -jar $JAR &
    echo "java -jar $JAR"
    cd ../../../
done

sleep $TEST_RUN_TIME

echo "Test finished. Killing java instances..."

killall java

echo "Java instances killed. Finished."

