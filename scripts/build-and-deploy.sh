#!/bin/bash

# Build and copy PEERS_NUM peers with resources to JAR_DIR/i/.
# Optional parameters: number_of_peers maven_profile_name

PEERS_NUM=4
MAVEN_JAR="target/nebulostore-0.5-SNAPSHOT.jar"
MAVEN_LIB="target/lib"
MAVEN_TARGET="peer"
BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"

if [ $1 ]; then
  PEERS_NUM=$1
fi

if [ $2 ]; then
  MAVEN_TARGET=$2
fi

rm -rf $BUILD_DIR
mvn clean install -P $MAVEN_TARGET

echo "Building done. Copying..."

rm -rf $JAR_DIR
mkdir -p $JAR_DIR
rsync -r $MAVEN_LIB $JAR_DIR/
for i in `seq 1 $PEERS_NUM`
do
    echo $i
    CURR_PATH="./$JAR_DIR/$i"
    rm -rf $CURR_PATH
    mkdir -p $CURR_PATH
    mkdir -p $CURR_PATH/logs
    mkdir -p $CURR_PATH/storage/bdb
    ln -s ../lib $CURR_PATH/lib
    cp $MAVEN_JAR $CURR_PATH/$JAR
    rsync -r --exclude=.svn resources $CURR_PATH
done

