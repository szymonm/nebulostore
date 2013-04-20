#!/bin/bash
# USAGE: select-sane-planet-lab-peers.sh [GOOD-PEER-LIMIT]
# GOOD_PEER_LIMIT - if presents sets the number of good peers to output.
#
# See hostavailabilitytest/test_host_availability.sh for detailed explanation of
# arguments and functionality.

ADD_ARG=""
if [[ $# -eq 1 ]]
then
    ADD_ARG="-l $1"
fi

bash -- $(dirname $0)/hostavailabilitytest/test_host_availability.sh $ADD_ARG -i
