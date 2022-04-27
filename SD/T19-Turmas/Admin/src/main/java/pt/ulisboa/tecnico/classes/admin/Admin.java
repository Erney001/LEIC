package pt.ulisboa.tecnico.classes.admin;

import java.util.List;
import java.util.Scanner;

import io.grpc.StatusRuntimeException;

public class Admin {
    private static final String ACTIVATE_CMD = "activate";
    private static final String DEACTIVATE_CMD = "deactivate";
    private static final String ACTIVATE_GOSSIP_CMD = "activateGossip";
    private static final String DEACTIVATE_GOSSIP_CMD = "deactivateGossip";
    private static final String GOSSIP_CMD = "gossip";
    private static final String DUMP_CMD = "dump";
    private static final String EXIT_CMD = "exit";
    private static final String DEBUG_CMD = "-debug";

    private static boolean _debug = false;

    /**
     * Helper method to print debug messages.
     */
    private static void debug(String debugMessage) {
        if (_debug)
            System.err.println("[DEBUG] " + debugMessage);
    }

    public static void main(String[] args) {

        if (args.length == 1 && DEBUG_CMD.equals(args[0]))
            _debug = true;
        else if (args.length > 0) { // check if no arguments received
            System.err.println("[ERROR] Non-recognition of arguments!");
            System.err.printf("Usage: java %s [-debug]%n", Admin.class.getName());
            System.exit(1);
        }

        // Creates client name server frontend to perform lookups to connect with appropriate servers
        NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost:5000");

        // Creates client frontend
        ClassServerFrontend frontend = new ClassServerFrontend(namingServerFrontend);

        // Run admin client procedure
        runAdminClient(frontend);

        // Terminates frontends channels
        frontend.shutdownChannel();
        namingServerFrontend.shutdown();

        System.exit(0);
    }

    public static void runAdminClient(ClassServerFrontend frontend) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.printf("%n> ");
            String cmd = scanner.nextLine();
            String[] cmdArgs = cmd.split(" ");
            debug("Command: " + cmd);

            switch (cmdArgs[0]) {
                case ACTIVATE_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Activate arguments are not recognized"
                                    + "\nUsage: activate [P|S]");
                        else {
                            String response = frontend.activate((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case DEACTIVATE_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Deactivate arguments are not recognized"
                                    + "\nUsage: deactivate [P|S]");
                        else {
                            String response = frontend.deactivate((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case ACTIVATE_GOSSIP_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Deactivate arguments are not recognized"
                                    + "\nUsage: deactivate [P|S]");
                        else {
                            String response = frontend.activateGossip((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case DEACTIVATE_GOSSIP_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Deactivate arguments are not recognized"
                                    + "\nUsage: deactivate [P|S]");
                        else {
                            String response = frontend.deactivateGossip((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case GOSSIP_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Deactivate arguments are not recognized"
                                    + "\nUsage: deactivate [P|S]");
                        else {
                            String response = frontend.gossip((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case DUMP_CMD:
                    try {
                        if (cmdArgs.length > 2 || (cmdArgs.length == 2 && !List.of("P", "S").contains(cmdArgs[1])))
                            System.err.println("[ERROR] Dump arguments are not recognized"
                                    + "\nUsage: dump [P|S]");
                        else {
                            String response = frontend.dump((cmdArgs.length == 2) ? cmdArgs[1] : "P");
                            if (response != null)
                                System.out.println(response);
                        }
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case EXIT_CMD:
                    scanner.close();
                    return;
                default:
                    System.err.println("[ERROR] Unknown command");
                    break;
            }
        }
    }
}
