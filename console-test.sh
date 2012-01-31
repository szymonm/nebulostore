#!/bin/bash

# Instructions:
#
# 1. Set a valid path in 'resources/conf/communication/BdbPeer_holder.xml'
#    to a dir with rw permissions (or leave '/tmp/' if it is ok).
# 2. Run ./console-test.sh
# 3. On first terminal (database holder):
#       cd build/jar/1/
#       java -jar Nebulostore.jar
# 4. On second terminal (proxy):
#       cd build/jar/2/
#       java -jar Nebulostore.jar
#       (wait a few seconds)
#       (press enter to see the command prompt)
#       (type "put" and press enter)
#       (expect message "Successfully received key (appkey)")
# 5. Now instance 2 of NebuloStore has called putKey() API method that additionally
#    created her topdir with one file. This instance itself is the only replica of
#    both dir and file. DHT contains addresses of topdir
#    replicas (= single entry with instance 2 address).
# 6. On first terminal:
#       (press enter to see the command prompt)
#       (type "get" and press enter)
#       (expect message "Successfully received file!")
# 7. Now first instance has called getNebuloFile() with a correct path and received
#    instance2's file. The file is saved into "pliczek" - check if it contains reasonable
#    data.
#
# 8. Details about keys and file names can be found in PutKeyModule.java and TextInterface.java.


BUILD_DIR="build"
JAR_DIR="build/jar"
JAR="Nebulostore.jar"
PEERS_NUM=3

rm -rf $BUILD_DIR
ant text-interface


echo "Building done. Copying..."

for i in `seq 1 $PEERS_NUM`
do
    path="./$JAR_DIR/$i"
    mkdir $path
    cp ./$JAR_DIR/*.jar ./$JAR_DIR/$i/
    cp -r ./$JAR_DIR/lib ./$JAR_DIR/$i/
    cp -r resources ./$JAR_DIR/$i/
done

cp ./resources/conf/communication/BdbPeer_holder.xml ./$JAR_DIR/1/resources/conf/communication/BdbPeer.xml

