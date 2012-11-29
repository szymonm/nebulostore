#!/bin/bash

# 0. copying data to remote hosts
bash prepare_test.sh

# 1. running jar on all hosts
bash run.sh "$@"

RESULT=$?

# 2. Copy logs
bash save_report.sh $RESULT $@

exit $RESULT
