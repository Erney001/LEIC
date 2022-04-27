package pt.ulisboa.tecnico.classes.student;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;

public class Student {
    static final String INVALID_STUDENT_ID_MESSAGE = "Invalid student id provided!\nStudent id must be of format alunoXXXX, where XXXX is a 4 digit integer";
    static final String INVALID_STUDENT_NAME_LENGTH_MESSAGE = "Invalid student name provided!\nStudent name must be between 3 and 30 letters long";
    static final String INVALID_CHARACTERS_IN_STUDENT_NAME = "Invalid student name provided!\nStudent name may only contain alphabet letters";

    private static final String LIST_CMD = "list";
    private static final String ENROLL_CMD = "enroll";
    private static final String EXIT_CMD = "exit";

    private static boolean _debug;
    private static String _studentId, _studentName;

    static boolean isValidStudentId(String studentId) {
        try {
            Integer.parseInt(studentId.substring(5));
            return studentId.startsWith("aluno") && studentId.length() == 9;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /** Helper method to print debug messages. */
    private static void debug(String debugMessage) {
        if (_debug)
            System.err.println("[DEBUG] " + debugMessage);
    }

    public static void main(String[] args) {
        // check number of arguments
        if (args.length < 2) {
            System.err.println("[ERROR] Argument(s) missing!");
            System.err.printf("Usage: java %s <student's id> <student's names> [-debug]%n", Student.class.getName());
            System.exit(1);
        }

        _studentId = args[0];
        _studentName = args[1];

        _debug = (args.length > 2 && args[args.length - 1].equals("-debug"));
        debug("Debug activated");

        for (int i = 2; i < (_debug ? args.length - 1 : args.length); i++) {
            _studentName = _studentName.concat(" " + args[i]);
        }

        // check if student id is valid
        if (!isValidStudentId(_studentId)) {
            System.err.println("[ERROR] " + INVALID_STUDENT_ID_MESSAGE);
            System.exit(1);
        }

        // check if student name is between 3 and 30 letters long
        else if (_studentName.length() < 3 || _studentName.length() > 30) {
            System.err.println("[ERROR] " + INVALID_STUDENT_NAME_LENGTH_MESSAGE);
            System.exit(1);
        }

        // check if student name only contains alphabet letters
        if (!_studentName.matches("^[a-zA-Z ]*$")) {
            System.err.println("[ERROR] " + INVALID_CHARACTERS_IN_STUDENT_NAME);
            System.exit(1);
        }

        debug("Student id: " + _studentId);
        debug("Student name: " + _studentName);

        NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost:5000");

        // Create client frontend
        ClassServerFrontend frontend = new ClassServerFrontend(namingServerFrontend);

        // Run student client procedure
        runStudentClient(frontend);

        frontend.shutdownChannel();
        namingServerFrontend.shutdown();

        System.exit(0);
    }


    private static void runStudentClient(ClassServerFrontend frontend) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.printf("%n> ");
            String cmd = scanner.nextLine();
            String[] cmdArgs = cmd.split(" ");
            debug("Command: " + cmd);

            switch (cmdArgs[0]) {
                case LIST_CMD:
                    try {
                        System.out.println(frontend.list());
                    } catch (StatusRuntimeException sre) {
                        System.err.println("Exception: " + sre.getStatus().getDescription());
                    } break;
                case ENROLL_CMD:
                    try {
                        System.out.println(frontend.enroll(_studentId, _studentName));
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
