#!/bin/bash
# Usage: test_host_availability.sh [-v] [-t TESTING_SERVER] [-l GOOD_PEER_LIMIT]
#                                  [-i [INPUT_FILE]] [HOSTNAME...]
# Tests planetlab hosts given as argument (either in file as a command-line
# argument) for ability to receive and send TCP and UDP packets. On default it
# prints on STDOUT only hostnames which are working correctly. 
# TESTING_SERVER (default is roti.mimuw.edu.pl) is a host which initiates
# connections to hosts being tested. TESTING_SERVER should be in a firewall-,
# nat-free enviroment to ensure correct result (We are testing for existance of
# restrictive nat,firewall on tested hosts, so nat on TESTING_SERVER would
# provide unwanted bias).
# 
# OPTIONS:
#  -v - turn on verbose printing
#  -t TESTING_SERVER - use non-default TESTING_SERVER 
#  -l GOOD_PEER_LIMIT - end working when at least GOOD_PEER_LIMIT number of
#  correct peers has been found
#  -i [INPUT_FILE] - use input file which contains names of the hosts to
#     test. On default it is "hosts-all.txt"

# dir from which this script was executed
EXEC_DIR=$(pwd)
cd $(dirname $0)

USAGE="$(basename $0) [-v] [-t TESTING_SERVER] [-l GOOD_PEER_LIMIT] \
{-i [INPUT_FILE]|HOSTNAME...}"
TESTING_SERVER="roti.mimuw.edu.pl"

USE_INPUT_FILE=false
INPUT_FILE="../hosts-all.txt"

SSH_OPTIONS="-o ConnectTimeout=1 -o StrictHostKeyChecking=no"

declare -a TESTED_HOSTS=()

declare -a BACKGROUND_JOBS=()
BACKGROUND_IDX=0

VERBOSE_OUTPUT=false

USE_GOOD_PEER_LIMIT=false
GOOD_PEER_LIMIT=0

function exit_cleanup() {
    for JOB_PID in ${BACKGROUND_JOBS[@]}
    do
        echo "Killing $JOB_PID" >&3
        kill $JOB_PID 2> /dev/null
    done
    if [[ $# -ne 0 ]]
    then
        ssh $SSH_OPTIONS mimuw_nebulostore@${1} "ps ax | grep FirewallTest | cut -d' ' -f2 | xargs kill 2> /dev/null; rm -f FirewallTestServer.class" >/dev/null 2>&1
    fi
    ssh mimuw_nebulostore@${TESTING_SERVER} "rm FirewallTestClient.class" >/dev/null 2>&1
    make clean >&3 2>&1
}

function SIGINT_handler() {
    echo "Control-C trap caught" >&3
    exit_cleanup
    exit 0
}

# PARSE OPTIONS
while getopts ":vt:i:l:" OPTION
do
  case $OPTION in
    v) VERBOSE_OUTPUT=true;;
    t) TESTING_SERVER=$OPTARG;;
    l) USE_GOOD_PEER_LIMIT=true;
       GOOD_PEER_LIMIT=$OPTARG;;
    i) USE_INPUT_FILE=true; 
       INPUT_FILE=${EXEC_DIR}/$OPTARG;;
   # DEFAULT
   *) 
       if [[ $OPTARG = "i" ]]
       then 
           USE_INPUT_FILE=true; 
       else
           ARG=$(($OPTIND-1)); echo "Unknown option option chosen: ${!ARG}."; 
       fi;;
  esac
done

shift $(($OPTIND - 1))

if $USE_INPUT_FILE
then
    for HOST in $(cat $INPUT_FILE)
    do
        TESTED_HOSTS+=($HOST)
    done
fi

while [[ $# -ne 0 ]]
do
    TESTED_HOSTS+=($1)
    shift
done

#USE 3rd file descriptor as a switch for verbose messages
if $VERBOSE_OUTPUT
then
    exec 3>&1
else
    exec 3>/dev/null
fi

trap 'SIGINT_handler' 2 # traps

make >&3 2>&1
echo "Copying FirewallTestClient to $TESTING_SERVER" >&3
scp FirewallTestClient.class mimuw_nebulostore@${TESTING_SERVER}: >&3 2>&1 
echo "Copying done" >&3

COUNTER=1
for HOST in ${TESTED_HOSTS[@]}
do
    if $USE_GOOD_PEER_LIMIT && [[ $GOOD_PEER_LIMIT -le 0 ]]
    then
        break;
    fi

    echo "$COUNTER. Testing $HOST" >&3
    scp $SSH_OPTIONS FirewallTestServer.class mimuw_nebulostore@${HOST}: >/dev/null 2>&1
    ssh $SSH_OPTIONS mimuw_nebulostore@${HOST} "java FirewallTestServer" >/dev/null 2>&1  &
    BACKGROUND_JOBS[$BACKGROUND_IDX]=$!
    BACKGROUND_IDX=$((BACKGROUND_IDX + 1))
    sleep 1
    if ssh mimuw_nebulostore@${TESTING_SERVER} "java FirewallTestClient ${HOST}" >/dev/null 2>&1
    then
        ((GOOD_PEER_LIMIT-=1))
        if $VERBOSE_OUTPUT
        then
            echo "SERVER: $HOST WORKS CORRECTLY"
        else
            echo $HOST
        fi
    else
        echo "SERVER: $HOST DOESN'T WORK" >&3
    fi
    
    # Kill and remove our UDP-TCP server from tested host
    ssh $SSH_OPTIONS mimuw_nebulostore@${HOST} "ps ax | grep FirewallTest | cut -d' ' -f2 | xargs kill 2> /dev/null; rm FirewallTestServer.class" >/dev/null 2>&1
    # Kill our UDP-TCP client from TESTING_SERVER in case it is still running
    ssh $SSH_OPTIONS mimuw_nebulostore@${TESTING_SERVER} "ps ax | grep FirewallTest | cut -d' ' -f2 | xargs kill 2> /dev/null;" >/dev/null 2>&1
    ((COUNTER+=1))
done
exit_cleanup
