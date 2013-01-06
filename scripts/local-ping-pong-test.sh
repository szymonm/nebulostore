#!/bin/bash

# Automatic local 3-peer ping-pong test.
# Please run from trunk level (./scripts/local-ping-ping-test.sh)
# WARNING: this script can take up to a minute to run.

BOOTSTRAP_DELAY=3

# Build and configure 3 regular peers.
echo "BUILDING ..."
./scripts/console-test.sh 3 peer > /dev/null

# Build test server as the last peer.
ant test-server > /dev/null
cp build/jar/Nebulostore.jar build/jar/3/

# Run peers.
echo "RUNNING ..."
cd build/jar/1
java -jar Nebulostore.jar 11 &
# Give bootstrap peer a few seconds to open sockets.
sleep $BOOTSTRAP_DELAY
cd ../2
java -jar Nebulostore.jar 22 &
cd ../3

java -jar Nebulostore.jar 33
if [ $? -eq 0 ]; then
  echo "SUCCESS"
else
  echo "FAILURE"
fi

# Kill remaining peers.
kill `jobs -p`
