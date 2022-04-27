package pt.tecnico.example.server;

import static io.grpc.Status.INVALID_ARGUMENT;

import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import pt.tecnico.example.ExampleServiceGrpc;
import pt.tecnico.example.GetNameRequest;
import pt.tecnico.example.NameResponse;
import pt.tecnico.example.SetNameRequest;
import pt.tecnico.example.server.domain.Example;
import pt.tecnico.example.server.domain.exception.InvalidNameException;

/** gRPC server operations implementation relying on domain objects. */
public final class ExampleServiceImpl extends ExampleServiceGrpc.ExampleServiceImplBase {
	private static final Logger LOGGER = Logger.getLogger(ExampleServiceImpl.class.getName());
	/** Domain root. */
	private final Example example = new Example();

	@Override
	public void getName(GetNameRequest request, StreamObserver<NameResponse> responseObserver) {
		LOGGER.info("getName()...");

		// Send the name back to the client.
		String name = example.getName();
		responseObserver.onNext(NameResponse.newBuilder().setName(name).build());
		responseObserver.onCompleted();
	}

	@Override
	public void setName(SetNameRequest request, StreamObserver<NameResponse> responseObserver) {
		String name = request.getProposedName();
		LOGGER.info("setName(" + name + ")...");
		try {
			example.setName(name);
			LOGGER.info("Name set to '" + name + "'.");

			// Send the name back to the client.
			name = example.getName();
			responseObserver.onNext(NameResponse.newBuilder().setName(name).build());
			responseObserver.onCompleted();

		} catch (InvalidNameException e) {
			LOGGER.info(e.getMessage());
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

}
