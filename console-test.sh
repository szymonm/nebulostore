#!/bin/bash

# Instructions:
#
# 1. Run ./console-test.sh
# 2. On first terminal:
#       cd build/jar/1/
#       java -jar Nebulostore.jar 11
# 3. On second terminal:
#       cd build/jar/2/
#       java -jar Nebulostore.jar 22
# 4. On third terminal (not on terminal 2!):
#       cd build/jar/3/
#       java -jar Nebulostore.jar 33
#       (press enter to see the command prompt)
#       type "write 22 123 zawartosc" and press enter
#       (expect messages "Successfully created new file." and "Successfully written x bytes.")
# 5. On terminal 2:
#       (press enter to see the command prompt)
#       type "read 22 123 pliczek" and press enter
#       (expect message "Successfully received file!")
# 6. The file is saved into "pliczek" - check if it contains reasonable data.
#
# 7. Details about commands and file names can be found in
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
PEERS_NUM=3

platform='unknown'
unamestr=`uname`
if [[ "$unamestr" == 'Darwin' ]]; then
  platform='mac'
else
  platform='linux'
fi

sequence='unknown'
if [[ $platform=='mac' ]]; then
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
done

cp ./resources/conf/communication/BdbPeer_holder.xml ./$JAR_DIR/1/resources/conf/communication/BdbPeer.xml
rm -rf /Users/bolek/tmp/baza_nebulo/
mkdir /Users/bolek/tmp/baza_nebulo/

