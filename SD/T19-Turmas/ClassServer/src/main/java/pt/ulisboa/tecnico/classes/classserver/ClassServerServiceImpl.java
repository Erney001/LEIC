package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.*;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;

import java.util.List;

public class ClassServerServiceImpl extends ClassServerServiceGrpc.ClassServerServiceImplBase{
    private final ClassState _classState;

    ClassServerServiceImpl(ClassState classState){
        _classState = classState;
    }

    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver){
        ClassesDefinitions.ClassState classState = request.getClassState();
        int version = classState.getVersion();
        int capacity = classState.getCapacity();
        boolean openEnrollments = classState.getOpenEnrollments();
        List<ClassesDefinitions.Student> enrolled = classState.getEnrolledList();
        List<ClassesDefinitions.Student> discarded = classState.getDiscardedList();

        ClassesDefinitions.ResponseCode code = _classState.propagateState(version, capacity, openEnrollments, enrolled, discarded);
        PropagateStateResponse response = PropagateStateResponse.newBuilder().setCode(code).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
