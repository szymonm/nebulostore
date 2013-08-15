#!/bin/bash

# Author: Grzegorz Milka
# Description: Extract hosts which are bootable and PLE/PLC from all_nodes.txt

sed '/PL[EC] boot/!d' all_nodes.txt | cut -d' ' -f1 > bootable_nodes.txt
