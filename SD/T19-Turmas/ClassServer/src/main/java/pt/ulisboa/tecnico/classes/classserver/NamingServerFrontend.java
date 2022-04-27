package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;

import java.util.ArrayList;
import java.util.List;

public class NamingServerFrontend {
    private final ManagedChannel channel;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    NamingServerFrontend(String namingServerTarget){
        channel = ManagedChannelBuilder.forTarget(namingServerTarget).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    void register(String serviceName, String target, List<String> qualifiers) {
        ClassServerNamingServer.RegisterRequest registerRequest = ClassServerNamingServer.RegisterRequest.newBuilder()
                .setServiceName(serviceName).setHostAndPort(target).addAllQualifiers(qualifiers).build();
        stub.register(registerRequest);
    }

    List<String> lookup(String serviceName, List<String> qualifiers) {
        ClassServerNamingServer.LookupRequest request =
                ClassServerNamingServer.LookupRequest.newBuilder().setServiceName(serviceName).addAllQualifiers(qualifiers).build();
        ClassServerNamingServer.LookupResponse response = stub.lookup(request);

        List<String> serviceServers = new ArrayList<>(response.getServiceServersList());

        return serviceServers;
    }

    void delete(String serviceName, String target) {
        ClassServerNamingServer.DeleteRequest request = ClassServerNamingServer.DeleteRequest.newBuilder()
                .setServiceName(serviceName)
                .setHostAndPort(target)
                .build();
        stub.delete(request);
    }

    void shutdown() {
        channel.shutdownNow();
    }
}
