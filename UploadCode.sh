#!/bin/bash

if [ "$1" == "" ]; then
	echo "Please enter a hostname"
	exit
fi

scp dist/FRCUserProgram.jar lvuser@$1:/home/lvuser
ssh -t lvuser@$1 "./etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r; sync"