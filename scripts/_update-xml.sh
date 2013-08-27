#!/bin/bash

# Usage:
# ./updateXml.sh base.xml updates.xml
#
# Updates base.xml with text values of respective nodes from updates.xml
# and prints them on standard output.
#
# Requires: saxon
# Uses: Oliver Becker xslt merge script (http://www2.informatik.hu-berlin.de/~obecker/XSLT/#merge).

SCRIPT_PATH=./scripts/xmlmerge/omerge.xslt

saxon-xslt $1 $SCRIPT_PATH with=$2 replace=true
