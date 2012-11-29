#!/bin/bash
#Author: Grzegorz Milka

. init_util.sh

DELAY=$1
APP_KEY=1

echo "Running BootstrapServer jar on $BOOTSTRAP_SERVER"
ssh -l $USER $BOOTSTRAP_SERVER "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY $BOOTSTRAP_SERVER $PRIMARY_HOST client > std.out.log 2> std.err.log &"
((APP_KEY++))

sleep 2

echo "Running jar on $PRIMARY_HOST"
ssh -l $USER $PRIMARY_HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar server > std.out.log 2> std.err.log &"

sleep 2

for HOST in $SLICE_HOSTS; do
    echo "Running jar on $HOST with appKey $APP_KEY"
    ssh -l $USER $HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; java -Xmx450m -Xss64k -jar Nebulostore.jar $APP_KEY $HOST $PRIMARY_HOST client > std.out.log 2> std.err.log &" 
    ((APP_KEY++))
done
