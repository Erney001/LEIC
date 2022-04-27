package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        final String DEBUG_CMD = "-debug";
        final String serviceName = "turmas";
        final boolean debug = DEBUG_CMD.equals(args[args.length - 1]);

        int port;
        final String target;
        String qualifier;
        List<String> qualifiers = new ArrayList<>();

        if (args.length - (debug? 1: 0) != 3) {
            System.err.println("[ERROR] Wrong usage of argument(s)!");
            System.err.printf("Usage: java %s host port qualifiers [%s]%n", ClassServer.class.getName(), DEBUG_CMD);

            System.exit(1);
        }

        port = Integer.parseInt(args[1]);
        target = args[0] + ":" + port;
        qualifier = args[2];
        qualifiers.add(qualifier);
        if (debug) System.err.println("Server on at " + target + " with qualifiers: " + qualifiers);

        NamingServerFrontend namingServerFrontend =  new NamingServerFrontend("localhost:5000");

        try {
            namingServerFrontend.register(serviceName, target, qualifiers);
            if (debug) System.err.println("[DEBUG] Server record registered in the naming server");
        } catch (StatusRuntimeException sre) {
            System.err.println("[Exception] Registering server entry occur: " + sre.getStatus().getDescription());
            System.exit(1);
        }

        ClassServerFrontend classServerFrontend = new ClassServerFrontend(namingServerFrontend, target);

        ClassState classState = new ClassState(debug, qualifiers.contains("P"), classServerFrontend);

        Server server = ServerBuilder.forPort(port).addService(new AdminServiceImpl(classState))
                .addService(new ClassServerServiceImpl(classState))
                .addService(new ProfessorServiceImpl(classState))
                .addService(new StudentServiceImpl(classState))
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                namingServerFrontend.delete(serviceName, target);
            } catch (StatusRuntimeException sre) {
                System.err.println("[Exception] Deleting server entry occur: " + sre.getStatus().getDescription());
                return;
            }
            if (debug) System.err.println("[DEBUG] Server removed its record from the naming server");

            classServerFrontend.shutdown();
            if (debug) System.err.println("[DEBUG] Server closed");
        }));

        server.start();
        if (debug) System.err.println("[DEBUG] Server started");

        server.awaitTermination();

        namingServerFrontend.shutdown();

        System.exit(0);
    }
}
