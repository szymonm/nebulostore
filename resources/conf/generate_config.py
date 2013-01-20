#!/usr/bin/python

"""
    Script that generates xml config from Peer.xml.template (passed as stdin).
    Provide values using command line argument. Provide at least the following:
        ./generate_config.py\
            --APP_KEY=123\
            --CLASS_NAME=org.nebulostore.appcore.Peer\
            --BOOTSTRAP_MODE=(client|server)\
            --BOOTSTRAP_ADDRESS=(address)\
            --CLI_PORT=(cli_port)\
            --BOOTSTRAP_PORT=(bootstrap_port)\
            --TOMP2P_PORT=(tp2p_port)\
            --BOOTSTRAP_TOMP2P_PORT=(port)\
            --BDB_TYPE=(storage-holder|proxy)\
            --BOOTSTRAP_MODE=(client|server)\
            --BOOTSTRAP_ADDRESS=(address)

    If some flags are missing, default values from Peer.xml.template are used.
"""

import sys

argmap = {}

for arg in sys.argv:
    lst = arg.split('=')
    if len(lst) == 2:
        argmap[lst[0][2:]] = lst[1]

for line in sys.stdin:
    parts = line.split('@')
    if len(parts) == 1:
        sys.stdout.write(line)
    else:
        sys.stdout.write(parts[0])
        for part in parts:
            if part.find('=') != -1:
                two = part.split('=')
                if two[0] in argmap:
                    print argmap[two[0]]
                else:
                    print two[1]
