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

#ssh -i $KEY_DIR/planetlab-key -l $USER $HOST "rm -rf $DEST_DIR && mkdir $DEST_DIR"
#scp -i $KEY_DIR/planetlab-key -r ./$TMP_DIR/* $USER@$HOST:~/$DEST_DIR/
rsync -rvu ./$TMP_DIR/* $USER@$HOST:~/$DEST_DIR/
ssh -i $KEY_DIR/planetlab-key -l $USER $HOST "cp -r lib $DEST_DIR/ "


rm -rf $TMP_DIR
