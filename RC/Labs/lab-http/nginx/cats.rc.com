server {
	listen 80;
	listen [::]:80;

	server_name cats.rc.com;

	root /var/www/cats.rc.com/html;
	index index.html;

	location / {
		gzip off;
		gunzip off;
		keepalive_timeout 0;

		# kill cache
        add_header Last-Modified $date_gmt;
        add_header Cache-Control 'no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0';
        if_modified_since off;
        expires off;
        etag off;

		try_files $uri $uri/ =404;
	}
}
