# gRPC client

This is an example of a gRPC client application.  
It reads a value from the standard input (`stdin`) and tries to set a name in the remote server.  
It demonstrates how to handle a gRPC error status returned on a call (in this case, when the proposed name is invalid) and how to write JUnit 5 integration tests.

The client depends on the contract module, where the protocol buffers shared between server and client are defined.
The client needs to know the interface to make remote calls.


## Instructions for using Maven

Make sure that you installed the contract module first.

To compile and run the client:

```
mvn compile exec:java
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
