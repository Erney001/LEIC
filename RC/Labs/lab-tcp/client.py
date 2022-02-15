import socket


def client_program():
    # Configure the hostname and port.
    host = '10.0.0.10'
    port = 50001

    # TO-DO: Create a new client socket.
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # TO-DO: Connect to the server.
    client_socket.connect((host, port))

    # Get data from user
    data = input('> ')

    # Continue sending data to the server until the end command.
    while data.lower().strip() != 'end':
        # TO-DO: Send data to the server (data.encode()).
        client_socket.send(data.encode())
	
        # TO-DO: Receive data back from the server (up to 1024 bytes).
        data = client_socket.recv(1024).decode()

        print('Data stream from server (\'' + host + '\',  ' + str(port) + '): ' + data)

        # Get data from user
        data = input('> ')

    # TO-DO: Close connection.
    client_socket.close()

    print('Connection terminated with server (\'' + host + '\', ' + str(port) + ')')


if __name__ == '__main__':
    client_program()
