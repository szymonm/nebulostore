#!/bin/bash
#Author: Grzegorz Milka

. init_util.sh

DELAY=$1
MAIN_CLASS="org.nebulostore.appcore.EntryPoint"

echo "Running BootstrapServer jar on $BOOTSTRAP_SERVER"
ssh -l $USER $BOOTSTRAP_SERVER "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; "\
"java -Xmx450m -Xss64k -cp 'lib/*:nebulostore-0.5.jar' org.nebulostore.appcore.EntryPoint  > std.out.log 2> std.err.log &"

sleep 2

    echo
echo "Running jar on $PRIMARY_HOST"
ssh -l $USER $PRIMARY_HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; "\
"java -Xmx450m -Xss64k -cp 'lib/*:nebulostore-0.5.jar' org.nebulostore.appcore.EntryPoint  > std.out.log 2> std.err.log &"

sleep 2

for HOST in $SLICE_HOSTS; do
    echo
    echo "Running jar on $HOST"
    ssh -l $USER $HOST "ulimit -s 64; ulimit -u 2000; cd $REMOTE_DIR; "\
    "java -Xmx450m -Xss64k -cp 'lib/*:nebulostore-0.5.jar' org.nebulostore.appcore.EntryPoint  > std.out.log 2> std.err.log &"
done
