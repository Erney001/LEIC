package pt.ulisboa.tecnico.classes.professor;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;

public class Professor {
    private static final String OPEN_ENROLLMENTS_CMD = "openEnrollments";
    private static final String CLOSE_ENROLLMENTS_CMD = "closeEnrollments";
    private static final String LIST_CMD = "list";
    private static final String CANCEL_ENROLLMENT_CMD = "cancelEnrollment";
    private static final String EXIT_CMD = "exit";
    private static final String DEBUG_CMD = "-debug";

    private static boolean _debug = false;

    public static void main(String[] args) {
        if (args.length == 1 && DEBUG_CMD.equals(args[0])){
            _debug = true;
        } else if (args.length != 0) {
            System.err.println("[ERROR] Non-recognition of arguments!");
            System.err.printf("Usage: java %s [-debug]%n", Professor.class.getName());
            System.exit(1);
        }

        NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost:5000");

        ClassServerFrontend stub = new ClassServerFrontend(namingServerFrontend);

        runProfessorClient(stub);

        stub.shutdownChannel();

        namingServerFrontend.shutdown();

        System.exit(0);
    }

    private static void runProfessorClient(ClassServerFrontend stub) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.printf("%n> ");
            String line = scanner.nextLine();
            String[] info = line.split(" ");
            debug("Command: " + line);

            if (OPEN_ENROLLMENTS_CMD.equals(info[0])) {
                try {
                    System.out.println(stub.openEnrollments(Integer.parseInt(info[1])));
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("[ERROR] <class capacity> argument not found");
                } catch (StatusRuntimeException e) {
                    System.err.println("Exception: " + e.getStatus().getDescription());
                }
            } else if (CLOSE_ENROLLMENTS_CMD.equals(info[0])) {
                try {
                    System.out.println(stub.closeEnrollments());
                } catch (StatusRuntimeException e) {
                    System.err.println("Exception: " + e.getStatus().getDescription());
                }
            } else if (LIST_CMD.equals(info[0])) {
                try {
                    System.out.println(stub.listClass());
                } catch (StatusRuntimeException e) {
                    System.err.println("Exception: " + e.getStatus().getDescription());
                }
            } else if (CANCEL_ENROLLMENT_CMD.equals(info[0])) {
                try {
                    String studentId = info[1];
                    if (Integer.parseInt(studentId.substring(5)) > 0
                            && studentId.startsWith("aluno")
                            && studentId.length() == 9){
                        System.out.println(stub.cancelEnrollment(info[1]));
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("[ERROR] <student id> argument not found");
                } catch (NumberFormatException e) {
                    System.err.println("[ERROR] <student id> argument wrong format");
                } catch (StatusRuntimeException e) {
                    System.err.println("Exception: " + e.getStatus().getDescription());
                }
            } else if (EXIT_CMD.equals(info[0])) {
                scanner.close();
                break;
            } else {
                System.err.println("[ERROR] Unknown command");
            }
        }
    }

    /**
     * Helper method to print debug messages.
     */
    private static void debug(String debugMessage) {
        if (_debug) {
            System.err.println("[DEBUG] " + debugMessage);
        }
    }
}
