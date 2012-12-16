#!/bin/bash
#Author: Grzegorz Milka

. init_util.sh

for HOST in $(get_all_hosts)
do
    echo "Cleaning logs on $HOST"
    ssh -l $USER $HOST "rm $REMOTE_DIR/*.log; rm -r $REMOTE_DIR/logs" 
done
