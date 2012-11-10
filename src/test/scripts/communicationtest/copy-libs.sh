#!/bin/bash

. init_util.sh

USER=`cat ssh-user.txt`

for HOST in $(get_all_hosts)
do
    echo "Copying lib to $HOST"
    rsync -rvu --size-only --cvs-exclude --delete \
        $BUILD_DIR/lib/* $USER@$HOST:~/lib/
done
