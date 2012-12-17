#!/bin/bash

# Instructions:
#
# 1. Run scripts/console-test.sh from 'trunk' level.
# 2. You can run up to 4 local instances.
#    On terminal nr i (1,2,3 or 4) run:
#       cd build/jar/i/
#       java -jar Nebulostore.jar ii
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


# Instructions to run on BDB DHT:
#
# 1. Change provider to "bdb" in resources/conf/communication/CommunicationPeer.xml
# 2. Set a valid path in 'resources/conf/communication/BdbPeer_holder.xml'
#    to a dir with rw permissions (or leave '/tmp/' if it is ok).
# 3. Continue with previous instructions.


BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"
PEERS_NUM=4

platform='unknown'
unamestr=`uname`
if [[ "$unamestr" == 'Darwin' ]]; then
  platform='mac'
else
  platform='linux'
fi

sequence='unknown'
if [[ $platform == 'mac' ]]; then
  sequence=`jot $PEERS_NUM`
else
  sequence=`seq 1 $PEERS_NUM`
fi

rm -rf $BUILD_DIR
ant text-interface


echo "Building done. Copying..."

for i in $sequence
do
    echo $i
    path="./$JAR_DIR/$i"
    mkdir $path
    cp ./$JAR_DIR/*.jar ./$JAR_DIR/$i/
    cp -r ./$JAR_DIR/lib ./$JAR_DIR/$i/
    cp -r resources ./$JAR_DIR/$i/
    sed "s/9987/1100$i/g" ./$JAR_DIR/$i/resources/conf/communication/CommunicationPeer.xml.local > ./$JAR_DIR/$i/resources/conf/communication/CommunicationPeer.xml.temp
    sed "s/10087/1200$i/g" ./$JAR_DIR/$i/resources/conf/communication/CommunicationPeer.xml.temp > ./$JAR_DIR/$i/resources/conf/communication/CommunicationPeer.xml
done

cp ./resources/conf/communication/BdbPeer_holder.xml ./$JAR_DIR/1/resources/conf/communication/BdbPeer.xml
cp ./$JAR_DIR/1/resources/conf/communication/CommunicationPeerServer.xml.local ./$JAR_DIR/1/resources/conf/communication/CommunicationPeer.xml
rm -rf /tmp/nebulostore/nebulo_baza
mkdir -p /tmp/nebulostore/nebulo_baza

