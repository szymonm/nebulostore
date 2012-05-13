#!/bin/bash
# usage number-of-slice-hosts out-file

SLICE_HOSTS_FILE=all-hosts.txt
KEY_LOCATION_FILE=key-location.txt

SLICE_HOSTS=`cat $SLICE_HOSTS_FILE | sort | uniq`
KEY_LOCATION=`cat $KEY_LOCATION_FILE`

CONNECTION_TIMEOUT=1


function read_free_stats() {
    OLDIFS=$IFS
    IFS=" "
    INPUT=$1
    [ ! -f $INPUT ]
    while read type total used free rest
    do
        echo `printf "%03d" $(($used*100/$total))`
    done < $INPUT;
    IFS=$OLDIFS
}

function check_host() {
    LOAD_REPORT_FILE="$1_`date +"%s"`_$RANDOM"
    HOST=$1
    touch $LOAD_REPORT_FILE
    truncate --size=0 $LOAD_REPORT_FILE

    scp -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -r ./cpu.sh mimuw_nebulostore@$HOST:~/ && \
    ssh -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -l mimuw_nebulostore $HOST 'free -m -o > load-report.txt && bash cpu.sh >> load-report.txt' && \
    scp -o "StrictHostKeyChecking no" -o ConnectTimeout=$CONNECTION_TIMEOUT -i $KEY_LOCATION/planetlab-key -r mimuw_nebulostore@$HOST:~/load-report.txt ./$LOAD_REPORT_FILE
    
    #Swap
    if [ `cat $LOAD_REPORT_FILE | wc -l` -gt 0 ] ; then
        cat $LOAD_REPORT_FILE | head -n 3 | tail -n 1 > $LOAD_REPORT_FILE.tmp
        INPUT="$LOAD_REPORT_FILE.tmp"
        echo -en "`read_free_stats $INPUT`\t" >> $LOAD_REPORT_FILE.all

        #Mem
        cat $LOAD_REPORT_FILE | head -n 2 | tail -n 1 > $LOAD_REPORT_FILE.tmp
        INPUT="$LOAD_REPORT_FILE.tmp"
        echo -en "`read_free_stats $INPUT`\t" >> $LOAD_REPORT_FILE.all

        cat $LOAD_REPORT_FILE | tail -n 1 | tr -d '\n' >> $LOAD_REPORT_FILE.all
        echo -e "\t$HOST" >> $LOAD_REPORT_FILE.all

        cat $LOAD_REPORT_FILE.all >> load-reports.txt

        rm $LOAD_REPORT_FILE.tmp
        rm $LOAD_REPORT_FILE.all
    fi;
    rm $LOAD_REPORT_FILE
    return 0;
}


touch load-reports.txt
truncate --size=0 load-reports.txt

IFS=$'\n'; 
for HOST in $SLICE_HOSTS; do 
    echo "Checking $HOST"
    check_host $HOST &
    sleep 1
done

sleep 10

echo "All reports:"
cat load-reports.txt

#cat load-reports.txt | grep -v $"^0[0-9][0-9]\t0\(7[0-9]\|[8[0-9]\|9[0-9]\)\t\(100\|09[5-9]\)" | sort > load-reports-sorted.txt
cat load-reports.txt | cut -f 1,2,4 | grep -v "9[0-9]" | grep -v "100" | sort > load-reports-sorted.txt


echo "All reports of feasible hosts sorted:"
cat load-reports-sorted.txt

cat load-reports-sorted.txt | head -n $1 > load-reports-best.txt
# Picking up slice hosts for tests
cat load-reports-best.txt | cut -f 3 > $2

echo "Best hosts:"
cat $2

