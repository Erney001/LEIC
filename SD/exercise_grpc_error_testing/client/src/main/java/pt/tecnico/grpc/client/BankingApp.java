package pt.tecnico.grpc.client;

import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.grpc.BankingServiceGrpc;
import pt.tecnico.grpc.Banking.RegisterRequest;
import pt.tecnico.grpc.Banking.*;
import io.grpc.StatusRuntimeException;

/** Client application main code. */
public class BankingApp {


	private static final String EXIT_CMD = "exit";
	private static final String REGISTER_CMD = "register";
	private static final String CONSULT_CMD = "consult";
	private static final String ZERO_CMD = "zero";
	private static final String TRANSFER_CMD = "transfer";

	public static void main(String[] args) {
		System.out.println(BankingApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", BankingApp.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);

		final ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

		// Create a blocking stub.

		final BankingServiceGrpc.BankingServiceBlockingStub stub = BankingServiceGrpc.newBlockingStub(channel);
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.printf("> Type the operation you wish to perform (`exit` to quit)%n> ");


			String line = scanner.nextLine();
			String[] info = line.split(" ");

			// exit
			if (EXIT_CMD.equals(line)) {
				scanner.close();
				break;
			}

			else if (REGISTER_CMD.equals(line)) {
				System.out.printf("> Type username you want to register%n> ");
				String client = scanner.nextLine();
				System.out.printf("> What is this user's initial balance%n> ");
				String balance = scanner.nextLine();
				stub.register(RegisterRequest.newBuilder().setClient(client).setBalance(Integer.parseInt(balance)).build());
				System.out.println("\n\n");
			}

			else if (CONSULT_CMD.equals(line)) {
				System.out.printf("> Type username whose balance you want to check%n> ");
				String client = scanner.nextLine();
				try {
					ConsultRequest request = ConsultRequest.newBuilder().setName(client).build();
					ConsultResponse response = stub.consult(request);
					System.out.println("Bank balance: " + response.getBalance());
				} catch (StatusRuntimeException e) {
					System.out.println("Caught exception with description: " + e.getStatus().getDescription());
				}
			} else if(ZERO_CMD.equals(line)){
				System.out.printf("> Type username you want to set balance to 0: ");
				String client = scanner.nextLine();
				try {
					ZeroRequest request = ZeroRequest.newBuilder().setName(client).build();
					ZeroResponse response = stub.zero(request);
				} catch (StatusRuntimeException e) {
					System.out.println("Caught exception with description: " + e.getStatus().getDescription());
				}
			} else if(TRANSFER_CMD.equals(info[0])){
				try {
					String from = info[1];
					String to = info[2];
					int amount = Integer.parseInt(info[3]);
					TransferRequest request = TransferRequest.newBuilder().setFrom(from).setTo(to).setAmount(amount).build();
					TransferResponse response = stub.transfer(request);
					System.out.println("Transference completed!");
				} catch(IndexOutOfBoundsException e){
					System.out.println("Caught exception with description: " + e);
				} catch (StatusRuntimeException e) {
					System.out.println("Caught exception with description: " + e.getStatus().getDescription());
				}
			}
		}

		channel.shutdownNow();
	}
}