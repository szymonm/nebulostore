#!/bin/bash

# Build and copy PEERS_NUM peers with resources to JAR_DIR.
# Optional parameters: number_of_peers ant_target_name

PEERS_NUM=4
BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"
TARGET=peer

if [ $1 ]; then
  PEERS_NUM=$1
fi

if [ $2 ]; then
  TARGET=$2
fi

rm -rf $BUILD_DIR
ant $TARGET

echo "Building done. Copying..."

for i in `seq 1 $PEERS_NUM`
do
    echo $i
    path="./$JAR_DIR/$i"
    mkdir $path
    cp ./$JAR_DIR/*.jar ./$JAR_DIR/$i/
    cp -r ./$JAR_DIR/lib ./$JAR_DIR/$i/
    cp -r resources ./$JAR_DIR/$i/
done

rm -rf /tmp/nebulostore
mkdir -p /tmp/nebulostore/nebulo_baza

