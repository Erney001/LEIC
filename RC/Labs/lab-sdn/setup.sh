cd ~/Downloads
git clone https://github.com/mininet/mininet
sed -i 's/git\:\/\//https\:\/\//g' ~/Downloads/mininet/util/install.sh
./mininet/util/install.sh -fw

pip install ryu
pip install gunicorn==20.1.0 eventlet==0.30.2

rm -r mininet
