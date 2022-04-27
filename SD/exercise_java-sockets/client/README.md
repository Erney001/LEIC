# Socket Server example


This example demonstrates the use of sockets in the Java platform.  
The server opens a TCP socket and blocks waiting for connections.
The port number is defined by an argument to the program.


## Instructions using Maven:

First, we need to launch the server and only then we can execute the client.

To compile and execute the server, execute the following commands on the _server_ dir:

```
mvn compile

mvn exec:java
```

To compile and execute the client, execute the following commands on a different terminal window on the _client_ dir:

```
mvn compile

mvn exec:java
```

In both terminal windows you should see the messages exchanged.