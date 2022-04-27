package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class NamingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        final String DEBUG_CMD = "-debug";
        final boolean debug = DEBUG_CMD.equals(args[args.length - 1]);
        int port;
        String target;

        if (args.length - (debug? 1: 0) != 2) {
            System.err.println("[ERROR] Wrong usage of argument(s)!");
            System.err.printf("Usage: java %s host port [%s]%n", NamingServer.class.getName(), DEBUG_CMD);
            System.exit(1);
        }

        port = Integer.parseInt(args[1]);
        target = args[0] + ":" + port;
        if (debug) System.err.println("Server on at " + target);

        Server server = ServerBuilder.forPort(port).addService(new NamingServerServiceImpl(new NamingServices(debug))).build();

        server.start();
        if (debug) System.err.println("Server started");

        server.awaitTermination();

        if (debug) System.err.println("Server closed");
        System.exit(0);
    }
}
