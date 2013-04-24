#!/bin/bash
# Usage: test_host_availability.sh [-v] [-t TESTING_SERVER] [-l GOOD_PEER_LIMIT]
#                                  [-p LISTENING_PORT] [-s TIMEOUT]
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
#  -p LISTENING_PORT - use this port for listening on server side
#  -s TIMEOUT- use given timeout in tests

# dir from which this script was executed
EXEC_DIR=$(pwd)
cd $(dirname $0)

USAGE="$(basename $0) [-v] [-t TESTING_SERVER] [-l GOOD_PEER_LIMIT] \
[-p LISTENING_PORT] [-s TIMEOUT] {-i [INPUT_FILE]|HOSTNAME...}"
export TESTING_SERVER="roti.mimuw.edu.pl"

USE_INPUT_FILE=false
INPUT_FILE="../hosts-all.txt"

declare -a TESTED_HOSTS=()

declare -a BACKGROUND_JOBS=()
export BACKGROUND_JOBS_SERVERFILE=$(mktemp)

export VERBOSE_OUTPUT=false

export USE_GOOD_PEER_LIMIT=false
export PEER_LIMIT_FILE=$(mktemp)

export LISTENING_PORT=9877
export CL_TIMEOUT=5

export SSH_OPTIONS="-o ConnectTimeout=$CL_TIMEOUT -o StrictHostKeyChecking=no"

function is_over_peer_limit() {
    if ! $USE_GOOD_PEER_LIMIT
    then
        return 1
    fi
    RET=1
    if [[$(wc $PEER_LIMIT_FILE) -ge $GOOD_PEER_LIMIT]]
    then
        RET=0
    fi
    return $RET
}

function dec_peer_limit() {
    echo 0 >> $PEER_LIMIT_FILE
}

export -f is_over_peer_limit
export -f dec_peer_limit

function cleanup_host() {
    SERVER=$1
    echo "Cleaning up at ${SERVER}" >&3
    ssh $SSH_OPTIONS mimuw_nebulostore@${SERVER} "ps ax | grep FirewallTest | sed 's/^ *//g' | cut -d' ' -f1 | xargs kill 2> /dev/null; rm -f FirewallTestServer.class" >/dev/null 2>&1
}

export -f cleanup_host

function exit_cleanup() {
    echo "Killing background jobs" >&3 2>&1
    # Kill all bacground jobs excluding the script itself and the commands below
    ps c --no-headers | grep -Ev 'ps|grep|cut|sed|xargs' | sed 's/^ *//' | cut -d' ' -f1 | grep -v "^$$$" | xargs -n 1 kill >/dev/null 2>&1
    cat $BACKGROUND_JOBS_SERVERFILE | xargs -P 15 -n 1 -i bash -c 'cleanup_host "$1"' _ {}
    ssh $SSH_OPTIONS mimuw_nebulostore@${TESTING_SERVER} "ps ax | grep FirewallTest | sed 's/^ *//' | cut -d' ' -f1 | xargs kill 2> /dev/null; rm FirewallTestClient.class" >/dev/null 2>&1
    make clean >&3 2>&1
}

function SIGINT_handler() {
    echo "Control-C trap caught" >&3
    exit_cleanup
    exit 0
}

# PARSE OPTIONS
while getopts ":vt:i:l:p:s:" OPTION
do
  case $OPTION in
    v) VERBOSE_OUTPUT=true;;
    t) TESTING_SERVER=$OPTARG;;
    l) USE_GOOD_PEER_LIMIT=true;
       GOOD_PEER_LIMIT=$OPTARG;;
    i) USE_INPUT_FILE=true; 
       INPUT_FILE=${EXEC_DIR}/$OPTARG;;
    s) CL_TIMEOUT=$OPTARG;
       SSH_OPTIONS="-o ConnectTimeout=$CL_TIMEOUT -o StrictHostKeyChecking=no";;
    p) LISTENING_PORT=$OPTARG;;
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

exec 4>&1

trap 'SIGINT_handler' 2 # traps

make >&3 2>&1
echo "Copying FirewallTestClient to $TESTING_SERVER" >&3
scp FirewallTestClient.class mimuw_nebulostore@${TESTING_SERVER}: >&3 2>&1 
echo "Copying done" >&3inriarennes2.irisa.fr


function TEST_HOST() {
    HOST=$1
    if is_over_peer_limit
    then
        exit 0
    fi

    echo "Testing $HOST" >&3
    echo $HOST >> $BACKGROUND_JOBS_SERVERFILE
    scp $SSH_OPTIONS FirewallTestServer.class mimuw_nebulostore@${HOST}: >/dev/null 2>&1
    ssh $SSH_OPTIONS mimuw_nebulostore@${HOST} "java FirewallTestServer ${LISTENING_PORT}" >/dev/null 2>&1  &
    sleep $CL_TIMEOUT
    if ssh $SSH_OPTIONS mimuw_nebulostore@${TESTING_SERVER} "java FirewallTestClient ${HOST} ${LISTENING_PORT}" >/dev/null 2>&1
    then
        if $VERBOSE_OUTPUT
        then
            echo "SERVER: $HOST WORKS CORRECTLY" >&3
        else
            echo $HOST >&4
        fi
        dec_peer_limit
    else
        echo "SERVER: $HOST DOESN'T WORK" >&3
    fi
    
    # Kill and remove our UDP-TCP server from tested host
    ssh $SSH_OPTIONS mimuw_nebulostore@${HOST} "ps ax | grep FirewallTest | sed 's/^ *//g' | cut -d' ' -f1 | xargs kill 2> /dev/null; rm FirewallTestServer.class" >/dev/null 2>&1
    exit 0
}
export -f TEST_HOST

ALL_HOSTS=""
for ((i=0; i<=$((${#TESTED_HOSTS[@]} - 1)); i++))
do
    ALL_HOSTS+=${TESTED_HOSTS[$i]}$'\n'
done

echo "$ALL_HOSTS" | xargs -P 15 -n 1 -i bash -c 'TEST_HOST "$1"' _ {} &>/dev/null
exit_cleanup
