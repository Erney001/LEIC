package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;

import java.util.Date;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {
    private final ClassState _classState;

    public StudentServiceImpl(ClassState classState) {
        _classState = classState;
    }

    @Override
    public void listClass(ListClassRequest request, StreamObserver<ListClassResponse> responseObserver) {
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
    public void enroll(EnrollRequest request, StreamObserver<EnrollResponse> responseObserver) {
        ResponseCode responseCode = _classState.enrollStudent(
                request.getStudent().getStudentId(),
                request.getStudent().getStudentName(),
                (int) new Date().getTime());
        EnrollResponse response = EnrollResponse.newBuilder().setCode(responseCode).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
