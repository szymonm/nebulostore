#!/bin/bash

# Author: Grzegorz Milka
# Description: Extract hosts which are bootable and PLE from all_nodes.txt

sed '/PLE boot/!d' all_nodes.txt | cut -d' ' -f1 > bootable_ple_nodes.txt
