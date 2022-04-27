package pt.ulisboa.tecnico.classes.student;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class NamingServerFrontend {
    private final ManagedChannel channel;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    NamingServerFrontend(String namingServerTarget) {
        channel = ManagedChannelBuilder.forTarget(namingServerTarget).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    List<String> lookup(String serviceName, List<String> qualifiers) {
        ClassServerNamingServer.LookupRequest request =
                ClassServerNamingServer.LookupRequest.newBuilder().setServiceName(serviceName).addAllQualifiers(qualifiers).build();
        ClassServerNamingServer.LookupResponse response = stub.lookup(request);

        List<String> serviceServers = new ArrayList<>(response.getServiceServersList());

        if (serviceServers.size() == 0) {
            System.err.println("No server providing the service");
        } else {
            Collections.shuffle(serviceServers);
        }

        return serviceServers;
    }

    void shutdown() {
        channel.shutdownNow();
    }
}
