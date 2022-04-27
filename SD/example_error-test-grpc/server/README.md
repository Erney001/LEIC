# gRPC server

This is an example of a gRPC server with a very simple domain.  
It implements a very basic server that offers a `set_name` method, which takes a non-blank name as the only parameter.  
If the value is invalid, the server throws a domain exception (then mapped to a gRPC `INVALID_ARGUMENT` status code).

The server depends on the contract module, where the protocol buffers shared between server and client are defined.
The server needs to know the interface to provide an implementation for it.


## Instructions for using Maven

Make sure that you installed the contract module first.

To compile and run the server:

```
mvn compile exec:java
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
