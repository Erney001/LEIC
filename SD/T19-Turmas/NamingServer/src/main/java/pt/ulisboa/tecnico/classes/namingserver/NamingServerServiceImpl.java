package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;

import java.util.ArrayList;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase {
    private final NamingServices _namingServices;

    NamingServerServiceImpl(NamingServices namingServices) {
        _namingServices = namingServices;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver){
        ServerEntry newServerEntry = new ServerEntry(request.getHostAndPort(), new ArrayList<>(request.getQualifiersList()));
        _namingServices.addServiceEntry(request.getServiceName(), newServerEntry);
        RegisterResponse response = RegisterResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver) {
        LookupResponse response = LookupResponse.newBuilder()
                .addAllServiceServers(_namingServices.getServiceProviders(request.getServiceName(), request.getQualifiersList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver){
        _namingServices.removeServiceEntry(request.getServiceName(), request.getHostAndPort());
        DeleteResponse response = DeleteResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
