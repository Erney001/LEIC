import socket
from _thread import *


def client_thread(conn, address):
    while True:
        # Receive a data stream up to 1024 bytes.
        data = conn.recv(1024).decode()
        if not data:
            # Break if no data stream is received.
            break
        print('Data stream from client ' + str(address) + ': ' + str(data))
        data = input('> ')
        # Send a data stream to the client.
        conn.send(data.encode())
    conn.close()
    print('Connection terminated with client ' + str(address))


def server_program():
    # Configure the hostname and port.
    host = '0.0.0.0'
    port = 50001

    # Instantiate.
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Bind the host address and port.
    server_socket.bind((host, port))
    # Configure to how many clients can the server listen to simultaneously.
    server_socket.listen(2)

    while True:
        # Accept a new connection.
        conn, address = server_socket.accept()
        print('Connection from: ' + str(address[0]) + ':' + str(address[1]))
        start_new_thread(client_thread, (conn, address, ))
    server_socket.close()


if __name__ == '__main__':
    server_program()
