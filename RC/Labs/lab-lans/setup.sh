#!/usr/bin/bash

set -eo pipefail

function sudo_run {
    echo "rc" | sudo -S $@
}

sudo_run apt install vlan bridge-utils isc-dhcp-server  -y
sudo_run modprobe --first-time 8021q
