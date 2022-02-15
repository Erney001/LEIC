#!/usr/bin/bash

host=$1
all=$2

while true
do
	sleep 10
	curl http://dogs.rc.com/admin --interface $host -u $all
done