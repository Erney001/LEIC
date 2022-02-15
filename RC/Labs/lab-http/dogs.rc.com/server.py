import http.server
import cgi
import base64
import json
import sys

from urllib.parse import urlparse, parse_qs
from os.path import exists, dirname, realpath

import os 

DIR_PATH = dirname(realpath(__file__))

class CustomServerHandler(http.server.BaseHTTPRequestHandler):

    def do_HEAD(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()

    def do_AUTHHEAD(self):
        self.send_response(401)
        self.send_header(
            'WWW-Authenticate', 'Basic realm="RC Realm"')
        self.send_header('Content-type', 'application/json')
        self.end_headers()

    def do_GET(self):
        key = self.server.get_auth_key()

        if 'admin' not in self.path:
            if self.path == '/':
                f = open(f'{DIR_PATH}/html/index.html', 'rb')
            else:
                file_exists = exists(f'{DIR_PATH}/html{self.path}')
                if not file_exists:
                    self.send_response(404)
                    return
                f = open(f'{DIR_PATH}/html{self.path}', 'rb')

            data = f.read()
            f.close()

            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(data)
            
            return

        ''' Present frontpage with user authentication. '''
        if self.headers.get('Authorization') == None:
            self.do_AUTHHEAD()

            response = {
                'success': False,
                'error': 'No auth header received'
            }

            self.wfile.write(bytes(json.dumps(response), 'utf-8'))

        elif self.headers.get('Authorization') == 'Basic ' + str(key):
            f = open(f'{DIR_PATH}/html/secret.html', 'rb')

            data = f.read()
            f.close()

            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(data)

        else:
            self.do_AUTHHEAD()

            response = {
                'success': False,
                'error': 'Invalid credentials'
            }

            self.wfile.write(bytes(json.dumps(response), 'utf-8'))

class CustomHTTPServer(http.server.HTTPServer):
    key = ''

    def __init__(self, address, handlerClass=CustomServerHandler):
        super().__init__(address, handlerClass)

    def set_auth(self, username, password):
        self.key = base64.b64encode(
            bytes('%s:%s' % (username, password), 'utf-8')).decode('ascii')

    def get_auth_key(self):
        return self.key


if __name__ == '__main__':
    assert(len(sys.argv) > 1)
    up=sys.argv[1].split(':')

    server = CustomHTTPServer(('', 10001))
    server.set_auth(up[0], up[1])
    server.serve_forever()