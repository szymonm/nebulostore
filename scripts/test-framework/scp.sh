#!/bin/bash
HOST=$1
BUILD_DIR=$2
KEY_DIR=$3
DEST_DIR=$4
TMP_DIR="tmp_`date +"%s"`_$RANDOM"
USER=`cat ssh-user.txt`


rm -rf $TMP_DIR
mkdir $TMP_DIR
cp -r $BUILD_DIR/* ./$TMP_DIR/
rm -rf ./$TMP_DIR/.jxta
rm -rf ./$TMP_DIR/.svn
rm -rf ./$TMP_DIR/*.log
rm -rf ./$TMP_DIR/lib

echo "rm -rf $DEST_DIR && mkdir $DEST_DIR"  | ssh -i $KEY_DIR/planetlab-key -l $USER $HOST 'bash -s'
scp -i $KEY_DIR/planetlab-key -r ./$TMP_DIR/* $USER@$HOST:~/$DEST_DIR/
echo "cp -r lib $DEST_DIR/ "  | ssh -i $KEY_DIR/planetlab-key -l $USER $HOST 'bash -s'


rm -rf $TMP_DIR
