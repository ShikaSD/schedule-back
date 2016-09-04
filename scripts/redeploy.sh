#!/bin/bash

archive=$(find /opt/play/archives -maxdepth 1 -type f -printf "%p\n"|sort|tail -1)
if [ -f ${archive} ]
    then
        sudo /opt/play/script/stop.sh
        mkdir /opt/play/temp/
        unzip ${archive} -d /opt/play/temp/
        folder=$(find /opt/play/temp -maxdepth 1 -type d -printf "%p\n"|sort|tail -1)
        if [ -d ${folder} ]
            then
                echo "${folder}"
                sudo rm -rf /opt/play/current/*
                mv ${folder}/* /opt/play/current/
                sudo /opt/play/script/start.sh
        fi
        rm -rf /opt/play/temp
fi