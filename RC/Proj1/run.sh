#!/bin/bash

set -euo pipefail

rm -f send.dat receive.dat sender-packets.log receiver-packets.log
dd if=/dev/urandom of=send.dat bs=1500 count=3

LD_PRELOAD="./log-packets.so" \
    PACKET_LOG="receiver-packets.log" \
    DROP_PATTERN="00" \
    ./file-receiver receive.dat 1234 3 &
RECEIVER_PID=$!
sleep .1

LD_PRELOAD="./log-packets.so" \
    PACKET_LOG="sender-packets.log" \
    DROP_PATTERN="01" \
    ./file-sender send.dat localhost 1234 3 || true

wait $RECEIVER_PID || true

diff -qs send.dat receive.dat || true
rm send.dat receive.dat || true

./generate-msc.sh msc.eps sender-packets.log receiver-packets.log
rm sender-packets.log receiver-packets.log
