package pt.tecnico.grpc.server;

import pt.tecnico.grpc.Banking.*;
import pt.tecnico.grpc.BankingServiceGrpc;
import static io.grpc.Status.INVALID_ARGUMENT;


import io.grpc.stub.StreamObserver;

public class BankingServiceImpl extends BankingServiceGrpc.BankingServiceImplBase {
	private Bank bank = new Bank();

	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
		bank.register(request.getClient(), request.getBalance());

		responseObserver.onNext(RegisterResponse.getDefaultInstance());
		responseObserver.onCompleted();
	}

	@Override
	public void consult(ConsultRequest request, StreamObserver<ConsultResponse> responseObserver) {
		if (!bank.isClient(request.getName())) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input has to be a valid user!").asRuntimeException());
		}

		Integer balance = bank.getBalance(request.getName());
		ConsultResponse response = ConsultResponse.newBuilder().setBalance(balance).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void zero(ZeroRequest request, StreamObserver<ZeroResponse> responseObserver) {
		if (!bank.isClient(request.getName())) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input has to be a valid user!").asRuntimeException());
		}

		bank.zero(request.getName());
		ZeroResponse response = ZeroResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
		if (!bank.isClient(request.getTo())) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Client to receive the money doesn't exist!").asRuntimeException());
		}

		if (!bank.isClient(request.getFrom())) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Client to send the money doesn't exist!").asRuntimeException());
		}

		bank.transfer(request.getFrom(), request.getTo(), request.getAmount());
		TransferResponse response = TransferResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
