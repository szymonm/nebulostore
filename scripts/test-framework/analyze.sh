#!/bin/bash
# usage: logs_folder

PRIMARY_HOST=host2.planetlab.informatik.tu-darmstadt.de

cat $1/$PRIMARY_HOST/testing.log  | grep succ | grep -v Not | grep -v WARM | grep Kad | sed "s/.*\[//" | sed "s/\][^\t]*\t/\t/" > ./results/dht_kad.txt
cat $1/$PRIMARY_HOST/testing.log  | grep succ | grep -v Not | grep -v WARM | grep Bdb | sed "s/.*\[//" | sed "s/\][^\t]*\t/\t/" > ./results/dht_bdb.txt

cat $1/$PRIMARY_HOST/testing.log  | grep succ | grep -v Not | grep -v WARM | sed "s/.*\[//" | sed "s/\][^\t]*//" | grep -v '[a-zA-Z]' > ./results/all.txt




