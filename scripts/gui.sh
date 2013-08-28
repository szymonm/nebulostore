#!/bin/bash

# Instructions:
#
# 1. Run scripts/gui.sh from 'trunk' level.
# 2. You can run up to 4 local instances.
#    On terminal nr i (1,2,3 or 4) run: (the app key will be 11, 22, etc.)
#       cd build/jar/i/
#       java -jar Nebulostore.jar
# 3. Wait 40 sec (!) for all peers to find each other.

source scripts/_utils.sh

JAR_DIR="build/jar"
PEERS_NUM=4
COMMON_ARGS="--class-name=org.nebulostore.gui.GUIController --bootstrap/address=localhost --bootstrap-server-tomp2p-port=10301 --bootstrap-port=10201"

./scripts/_build-and-deploy.sh $PEERS_NUM


for ((i=1; i<=$PEERS_NUM; i++))
do
    PARAMS="$COMMON_ARGS --app-key=$i$i --bootstrap/mode=client --comm-cli-port=1010$i --tomp2p-port=1030$i --bdb-peer/type=proxy"
    generateConfigFile "$PARAMS" $JAR_DIR/$i/resources/conf
done

PARAMS="$COMMON_ARGS --app-key=11 --bootstrap/mode=server --comm-cli-port=10101 --tomp2p-port=10301 --bdb-peer/type=storage-holder"
generateConfigFile "$PARAMS" $JAR_DIR/1/resources/conf
