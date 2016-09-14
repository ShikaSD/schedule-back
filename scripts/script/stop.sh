#!/bin/bash

pid=$(cat /opt/play/current/RUNNING_PID)

if [ -f /opt/play/current/RUNNING_PID ]
    then
        start-stop-daemon --stop --pid ${pid}
fi