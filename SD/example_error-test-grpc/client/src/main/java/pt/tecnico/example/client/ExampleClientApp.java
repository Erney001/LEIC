package pt.tecnico.example.client;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;
import pt.tecnico.example.GetNameRequest;
import pt.tecnico.example.NameResponse;
import pt.tecnico.example.SetNameRequest;

/** Client application main code. */
public class ExampleClientApp {
	private static final String EXIT_CMD = "exit";
	private static final String GET_CMD = "get";

	public static void main(String[] args) {
		System.out.println(ExampleClientApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", ExampleClientApp.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);

		try (ExampleFrontend frontend = new ExampleFrontend(host, port); Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.printf("> Type the name to set (`exit` to quit)%n> ");
				try {
					String line = scanner.nextLine();

					// exit
					if (EXIT_CMD.equals(line))
						break;

					// get name
					if (GET_CMD.equals(line)) {
						NameResponse getResponse = frontend.getName(GetNameRequest.getDefaultInstance());
						System.out.println("The current name is '" + getResponse.getName() + "'.");
						continue;
					}

					// set name
					SetNameRequest setRequest = SetNameRequest.newBuilder().setProposedName(line).build();
					NameResponse setResponse = frontend.setName(setRequest);
					System.out.println("The name was set to '" + setResponse.getName() + "'.");

				} catch (StatusRuntimeException e) {
					System.out.println(e);
				}
			}

		} finally {
			System.out.println("> Closing");
		}
	}
}
