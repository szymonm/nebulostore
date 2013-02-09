#!/bin/bash
#Copies CUR_DIR (can be a file) to HOST:DEST_DIR using rsync
HOST=$1
BUILD_DIR=$2
DEST_DIR=$3
USER=`cat ssh-user.txt`

#GM size-only because servers have clocks 2 hours behind so we want to avoid it.
rsync -rvu --size-only --cvs-exclude $BUILD_DIR/target/nebulostore-0.5.jar $USER@$HOST:~/$DEST_DIR
rsync -rvu --size-only --cvs-exclude $BUILD_DIR/resources $USER@$HOST:~/$DEST_DIR
ssh -l $USER $HOST "cp -r lib $DEST_DIR/ "
