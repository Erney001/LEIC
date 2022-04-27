package pt.tecnico.ttt.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.ttt.*;

public class TTTServiceImpl extends TTTGrpc.TTTImplBase {

	/** Game implementation. */
	private ttt = new TTTGame();

	@Override
	public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
		// StreamObserver is used to represent the gRPC stream between the server and
		// client in order to send the appropriate responses (or errors, if any occur).

		CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

	@Override
	public void play(PlayRequest request, StreamObserver<PlayResponse> responseObserver){
		PlayResponse response = PlayResponse.newBuilder().setRes(ttt.play(request.getRow(), request.getCol(), request.getPlayer())).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void checkWinner(CheckWinnerRequest request, StreamObserver<CheckWinnerResponse> responseObserver ){
		CheckWinnerResponse response = CheckWinnerResponse.newBuilder().setResult(ttt.checkWinner()).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void ultimaJogada(UltimaRequest request, StreamObserver<UltimaResponse> responseObserver ){
		UltimaResponse response = UltimaResponse.newBuilder().setRow(ttt.getLRow()).setCol(ttt.getLCol()).setPlayer(ttt.getLPlayer()).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
