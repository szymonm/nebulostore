#!/bin/bash

# Instructions:
#
# 1. Run scripts/console-test.sh from 'trunk' level.
# 2. You can run up to 4 local instances.
#    On terminal nr i (1,2,3 or 4) run: (the app key will be 11, 22, etc.)
#       cd build/jar/i/
#       java -jar Nebulostore.jar
# 3. Wait 40 sec (!) for all peers to find each other.
# 4. Play with it using write, read or delete.
#    For example:
#    On terminal 2:
#       write 33 123 zawartosc
#    On terminal 1:
#       read 33 123 plik.txt
#       (check if plik.txt contains "zawartosc")
# 5. Details about commands and file names can be found in
#    TextInterface.java - feel free to experiment!

JAR_DIR="build/jar"
PEERS_NUM=4
COMMON_ARGS="--CLASS_NAME=org.nebulostore.systest.textinterface.TextInterface --BOOTSTRAP_ADDRESS=localhost --BOOTSTRAP_TOMP2P_PORT=10301 --BOOTSTRAP_PORT=10201"

./scripts/_build-and-deploy.sh $PEERS_NUM

cd $JAR_DIR
for ((i=1; i<=$PEERS_NUM; i++))
do
    cd $i/resources/conf
    ./generate_config.py $COMMON_ARGS --APP_KEY=$i$i --BOOTSTRAP_MODE=client --CLI_PORT=1010$i --TOMP2P_PORT=1030$i --BDB_TYPE=proxy < Peer.xml.template > Peer.xml
    cd ../../../
done

cd 1/resources/conf
./generate_config.py $COMMON_ARGS --APP_KEY=11 --BOOTSTRAP_MODE=server --CLI_PORT=10101 --TOMP2P_PORT=10301 --BDB_TYPE=storage-holder < Peer.xml.template > Peer.xml

