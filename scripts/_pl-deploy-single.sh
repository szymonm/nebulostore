#!/bin/bash

cd $(dirname $0)
. _constants.sh

echo "   "$2
ssh -o $SSH_OPTIONS -l $USER $2 "mkdir -p $REMOTE_DIR; rm -rf $REMOTE_DIR/logs* $REMOTE_DIR/storage* $REMOTE_DIR/resources*"
rsync -rul ../build/jar/1/* $USER@$2:~/$REMOTE_DIR/
rsync -rul ../Peer.xml.$1 $USER@$2:~/$REMOTE_DIR/resources/conf/Peer.xml
rm ../Peer.xml.$1
