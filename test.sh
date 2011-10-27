#!/bin/bash
rm -rf ./jar/*

ant

peers_num=5

for i in `seq 1 $peers_num`
do
    path="./jar/$i"
    mkdir $path
    cp ./jar/RunCommunicationModule.jar ./jar/$i/
    cp ./log4j.xml ./jar/$i/
done


echo "copying done. Running..."

for i in `seq 1 $peers_num`
do
    path="RunCommunicationModule.jar"
    cd ./jar/$i
    java -jar $path &
    cd ../
done


sleep 100
killall java


