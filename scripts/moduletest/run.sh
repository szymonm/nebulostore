#!/bin/bash
#Author: Grzegorz Milka

cd build
java -Xmx450m -Xss2M -cp 'lib/*:nebulostore-0.5.jar' \
org.nebulostore.appcore.EntryPoint > stdout.log 2> stderr.log
