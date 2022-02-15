#!/usr/bin/bash

set -eo pipefail

if [ "$#" -lt 1 ]; then
    echo "Usage: ./setup.sh <istid>"
    exit 1
fi

istid=$1
debug=$2

if [ "$debug" != "-d" ]; then
    exec &>/dev/null
fi

function sudo_run {
    echo "rc" | sudo -S $@
}

sudo_run apt install curl -y
sudo_run apt install python3 -y

garbage="nqzva:fhcre_qhcre_cnffj0eq_"
salt=$(python3 -c "import hashlib;print(hashlib.md5(bytes('$istid',encoding='utf8')).hexdigest()[:4])")
preamble=$(python3 -c "import codecs;print(codecs.decode(\"$garbage\", \"rot13\"))")
all=$preamble$salt

SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
HOST_IP="$(ip a | head -n 9 | tail -n 1 | awk -F ' ' '{print $2}' | sed -e 's/\/.*//g')"

function cleanup {
    sudo_run killall .admin.sh > /dev/null 2>&1 || true
    sudo_run killall python3 > /dev/null 2>&1 || true
}
trap cleanup EXIT

sudo_run cp $SCRIPT_DIR/hosts /etc/hosts

sudo_run cp -r $SCRIPT_DIR/cats.rc.com /var/www/
sudo_run chown -R $USER:$USER /var/www/cats.rc.com/html

sudo_run chmod -R 755 /var/www

sudo_run cp $SCRIPT_DIR/nginx/nginx.conf /etc/nginx/
sudo_run cp $SCRIPT_DIR/nginx/cats.rc.com /etc/nginx/sites-available/
sudo_run cp $SCRIPT_DIR/nginx/dogs.rc.com /etc/nginx/sites-available/

sudo_run ln -sf /etc/nginx/sites-available/cats.rc.com /etc/nginx/sites-enabled/
sudo_run ln -sf /etc/nginx/sites-available/dogs.rc.com /etc/nginx/sites-enabled/

sudo_run nginx -t
sudo_run systemctl restart nginx

chmod +x $SCRIPT_DIR/.admin.sh 

$SCRIPT_DIR/.admin.sh $HOST_IP $all > /dev/null 2>&1 &
python3 $SCRIPT_DIR/dogs.rc.com/server.py $all