#!/bin/bash
# usage: logs_folder

cat $1/roti.mimuw.edu.pl/testing.log  | grep succ | grep Kad | sed "s/.*\[//" | sed "s/\][^\t]*\t/\t/" > ./results/dht_kad.txt
cat $1/roti.mimuw.edu.pl/testing.log  | grep succ | grep Bdb | sed "s/.*\[//" | sed "s/\][^\t]*\t/\t/" > ./results/dht_bdb.txt

cat $1/roti.mimuw.edu.pl/testing.log  | grep succ | sed "s/.*\[//" | sed "s/\][^\t]*//" | grep -v '[a-zA-Z]' > ./results/all.txt




