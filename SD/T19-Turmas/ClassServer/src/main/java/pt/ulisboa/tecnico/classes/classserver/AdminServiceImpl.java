package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
    private final ClassState _classState;

    public AdminServiceImpl(ClassState classState){
        _classState = classState;
    }

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        ResponseCode code = _classState.activate();
        ActivateResponse response = ActivateResponse.newBuilder()
                .setCode(code)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        ResponseCode code = _classState.deactivate();
        DeactivateResponse response = DeactivateResponse.newBuilder()
                .setCode(code)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void activateGossip(ActivateGossipRequest request, StreamObserver<ActivateGossipResponse> responseObserver) {
        ResponseCode code = _classState.activateGossip();
        ActivateGossipResponse response = ActivateGossipResponse.newBuilder()
                .setCode(code)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deactivateGossip(DeactivateGossipRequest request, StreamObserver<DeactivateGossipResponse> responseObserver) {
        ResponseCode code = _classState.deactivateGossip();
        DeactivateGossipResponse response = DeactivateGossipResponse.newBuilder()
                .setCode(code)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
        ResponseCode code = _classState.gossip();
        GossipResponse response = GossipResponse.newBuilder()
                .setCode(code)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void dump(DumpRequest request, StreamObserver<DumpResponse> responseObserver) {
        DumpResponse response = DumpResponse.newBuilder()
                .setCode(ResponseCode.OK)
                .setClassState(_classState.toProto())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
