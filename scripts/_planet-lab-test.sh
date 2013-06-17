#!/bin/bash

# Automatic local N-peer test.
# Please run from trunk level (./scripts/planet-lab-test.sh)
#
# Make sure that you have uploaded necessary libraries to the host
# Use scripts/upload-libs-to-planet-lab.sh for that purpose.
#
# Optional parameters: peer_class_name test_server_class_name number_of_peers number_of_test_clients number_of_iterations host_list
# Prints "SUCCESS" or "FAILURE"

. scripts/_constants.sh

PEERNAME="org.nebulostore.systest.TestingPeer"
TESTNAME="org.nebulostore.systest.pingpong.PingPongServer"
PEER_NUM=3
TEST_CLIENTS_NUM=2
TEST_ITER=3
HOST_LIST="scripts/hosts.txt"

LOG_DIR=logs

BOOTSTRAP_DELAY=5
MAX_THREADS=15

if [ $1 ]; then
  PEERNAME=$1
  TESTNAME=$2
  PEER_NUM=$3
  TEST_CLIENTS_NUM=$4
  TEST_ITER=$5
  HOST_LIST=$6
fi

BOOTSTRAP_PEER=`cat $HOST_LIST | head -n 1`



echo "["`date +"%T"`"] BUILDING ..."
./scripts/_build-and-deploy.sh 1 peer > /dev/null
./scripts/_generate-config-files.sh $PEERNAME $TESTNAME $PEER_NUM $TEST_CLIENTS_NUM $TEST_ITER $BOOTSTRAP_PEER



echo "["`date +"%T"`"] COPYING ..."
i=1
PAIRS=""
CLIENT_HOSTS=""
for host in `cat $HOST_LIST`
do
    PAIRS+="$i $host "
    if [ $i -ne 1 ]; then CLIENT_HOSTS+="$host "; fi
    if [ $i -eq $PEER_NUM ]; then break; else ((i++)); fi
done
echo $PAIRS | xargs -P $MAX_THREADS -n 2 ./scripts/_pl-deploy-single.sh



echo "["`date +"%T"`"] RUNNING ..."

run_clients() {
    sleep $BOOTSTRAP_DELAY
    echo $CLIENT_HOSTS | xargs -P $MAX_THREADS -n 1 ./scripts/_pl-run-single.sh
    echo "["`date +"%T"`"] WAITING FOR TEST TO FINISH ..."
}

run_clients &
echo "  " $BOOTSTRAP_PEER
ssh -o $SSH_OPTIONS -l $USER $BOOTSTRAP_PEER "cd $REMOTE_DIR; $JAVA_EXEC -jar Nebulostore.jar > logs/stdout.log 2> logs/stderr.log && echo 'OK' > logs/exit_code || echo 'ERROR' > logs/exit_code"



echo "["`date +"%T"`"] KILLING PEERS AND COLLECTING LOGS ..."
rm -rf $LOG_DIR
mkdir $LOG_DIR

echo $PAIRS | xargs -P $MAX_THREADS -n 2 ./scripts/_pl-kill-and-get-logs-single.sh



echo -n "["`date +"%T"`"] TEST RESULT: "
cat $LOG_DIR/logs_1/exit_code

