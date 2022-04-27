package pt.ulisboa.tecnico.classes.admin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NamingServerFrontend {
    private final ManagedChannel channel;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    NamingServerFrontend(String namingServerTarget) {
        channel = ManagedChannelBuilder.forTarget(namingServerTarget).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    String lookup(String serviceName, List<String> qualifiers) {
        ClassServerNamingServer.LookupRequest request =
                ClassServerNamingServer.LookupRequest.newBuilder().setServiceName(serviceName).addAllQualifiers(qualifiers).build();
        ClassServerNamingServer.LookupResponse response = stub.lookup(request);

        List<String> serviceServers = new ArrayList<>(response.getServiceServersList());
        String target = "";

        int size = serviceServers.size();
        if (size == 0) {
            System.err.println("No " + qualifiers.get(0) + " server providing the service");
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(size);
            target = serviceServers.get(index);
        }

        return target;
    }

    void shutdown() {
        channel.shutdownNow();
    }
}
