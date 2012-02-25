#!/bin/bash
HOST=$1
BUILD_DIR=$2
KEY_DIR=$3
DEST_DIR=$4

rm -rf tmp
mkdir tmp
cp -r $BUILD_DIR/* ./tmp/
rm -rf ./tmp/.jxta
rm -rf ./tmp/.svn
rm -rf ./tmp/*.log
echo "rm -rf $DEST_DIR && mkdir $DEST_DIR"  | ssh -i $KEY_DIR/planetlab-key -l mimuw_nebulostore $HOST 'bash -s'
scp -i $KEY_DIR/planetlab-key -r ./tmp/* mimuw_nebulostore@$HOST:~/$DEST_DIR/
