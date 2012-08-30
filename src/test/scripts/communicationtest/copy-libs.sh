#!/bin/bash

. init_util.sh

BUILD_LOCATION_FILE=build-location.txt
BUILD_DIR=`cat $BUILD_LOCATION_FILE`
BUILD_DIR=$BUILD_DIR/$TEST_NAME

USER=`cat ssh-user.txt`

for HOST in $(get_all_hosts)
do
    echo "Copying lib to $HOST"
    rsync -rvu --size-only --cvs-exclude --delete \
        $BUILD_DIR/lib $USER@$HOST:~/lib
done
