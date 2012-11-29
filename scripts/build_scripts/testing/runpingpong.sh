#!/bin/bash

# Assuming we are in build_scripts/testing/ directory

cd $(dirname $0)
cd ../../

BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"
TEST_NAME="runpingpong"
path="./$JAR_DIR/$TEST_NAME"

platform='unknown'
unamestr=`uname`
if [[ "$unamestr" == 'Darwin' ]]; then
  platform='mac'
else
  platform='linux'
fi

rm -rf $BUILD_DIR

ant $TEST_NAME

echo "Building $TEST_NAME done. Copying..."

mkdir $path
cp ./$JAR_DIR/*.jar $path
cp -r ./$JAR_DIR/lib $path
cp -r resources $path
