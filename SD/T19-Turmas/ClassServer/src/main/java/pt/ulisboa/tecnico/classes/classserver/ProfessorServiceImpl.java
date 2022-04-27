package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;

public class ProfessorServiceImpl extends ProfessorServiceGrpc.ProfessorServiceImplBase {
    private final ClassState _classState;

    public ProfessorServiceImpl(ClassState classState){
        _classState = classState;
    }

    @Override
    public void openEnrollments(OpenEnrollmentsRequest request, StreamObserver<OpenEnrollmentsResponse> responseObserver){
        ResponseCode code = _classState.openEnrollments(request.getCapacity());
        OpenEnrollmentsResponse response = OpenEnrollmentsResponse.newBuilder().setCode(code).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void closeEnrollments(CloseEnrollmentsRequest request, StreamObserver<CloseEnrollmentsResponse> responseObserver){
        ResponseCode code = _classState.closeEnrollments();
        CloseEnrollmentsResponse response = CloseEnrollmentsResponse.newBuilder().setCode(code).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listClass(ListClassRequest request, StreamObserver<ListClassResponse> responseObserver){
        ResponseCode responseCode = _classState.checkServerState();
        ListClassResponse.Builder responseBuilder = ListClassResponse.newBuilder();

        if (responseCode == ResponseCode.OK) {
            responseBuilder.setClassState(_classState.toProto());
        }

        responseBuilder.setCode(responseCode);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelEnrollment(CancelEnrollmentRequest request, StreamObserver<CancelEnrollmentResponse> responseObserver){
        ResponseCode code = _classState.cancelEnrollment(request.getStudentId(), false);
        CancelEnrollmentResponse response = CancelEnrollmentResponse.newBuilder().setCode(code).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
