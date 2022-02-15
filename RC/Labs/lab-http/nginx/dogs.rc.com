server {
	listen 80;
	listen [::]:80;

	server_name dogs.rc.com;

	location / {
		proxy_pass http://127.0.0.1:10001/;
	}
}
