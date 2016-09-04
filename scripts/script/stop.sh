#!/bin/bash

pid=$(pidof -s java)

start-stop-daemon --stop --pid ${pid}