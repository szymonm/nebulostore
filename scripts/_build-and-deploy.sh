#!/bin/bash

# Build and copy PEERS_NUM peers with resources to JAR_DIR/i/.
# Optional parameters: number_of_peers maven_profile_name

source scripts/_jar-properties.sh
source scripts/_utils.sh

PEERS_NUM=4
MAVEN_JAR="target/$JAR_NAME"
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
buildNebulostore  $MAVEN_TARGET

echo "Building done. Copying..."

rm -rf $JAR_DIR
mkdir -p $JAR_DIR
rsync -r $MAVEN_LIB $JAR_DIR/
for ((i=1; i<=$PEERS_NUM; i++))
do
    echo $i
    CURR_PATH="./$JAR_DIR/$i"
    createNebuloLocalArtifact $CURR_PATH $MAVEN_JAR $JAR
done

