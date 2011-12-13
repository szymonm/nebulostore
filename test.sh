#!/bin/bash

BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"
PEERS_NUM=3
TEST_RUN_TIME=60

rm -rf $BUILD_DIR

ant $1


echo "Building done. Copying..."

for i in `seq 1 $PEERS_NUM`
do
    path="./$JAR_DIR/$i"    
    mkdir $path
    cp ./$JAR_DIR/*.jar ./$JAR_DIR/$i/
    cp -r ./$JAR_DIR/lib ./$JAR_DIR/$i/
    cp -r resources ./$JAR_DIR/$i/
done

echo "Copying done. Running..."

for i in `seq 1 $PEERS_NUM`
do
    cd ./$JAR_DIR/$i
    java -jar $JAR $i&
    echo "java -jar $JAR $i"
    cd ../../../
done

# Special mod for bdb dht impl
cp ./resources/conf/communication/BdbPeer_holder.xml ./$JAR_DIR/1/resources/conf/communication/BdbPeer.xml

sleep $TEST_RUN_TIME

echo "Test finished. Killing java instances..."

killall java

echo "Java instances killed. Finished."

