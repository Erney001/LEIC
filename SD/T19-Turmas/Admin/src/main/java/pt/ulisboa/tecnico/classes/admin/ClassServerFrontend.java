package pt.ulisboa.tecnico.classes.admin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;

import java.util.List;

public class ClassServerFrontend {

    private final NamingServerFrontend _namingServerFrontend;
    private ManagedChannel _channel;
    private AdminServiceGrpc.AdminServiceBlockingStub _stub;

    ClassServerFrontend(NamingServerFrontend namingServerFrontend) {
        _namingServerFrontend = namingServerFrontend;
    }

    String activate(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        ActivateResponse response = _stub.activate(ActivateRequest.getDefaultInstance());
        return Stringify.format(response.getCode());
    }

    String deactivate(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        DeactivateResponse response = _stub.deactivate(DeactivateRequest.getDefaultInstance());
        return Stringify.format(response.getCode());
    }

    String activateGossip(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        ActivateGossipResponse response = _stub.activateGossip(ActivateGossipRequest.getDefaultInstance());
        return Stringify.format(response.getCode());
    }

    String deactivateGossip(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        DeactivateGossipResponse response = _stub.deactivateGossip(DeactivateGossipRequest.getDefaultInstance());
        return Stringify.format(response.getCode());
    }

    String gossip(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        GossipResponse response = _stub.gossip(GossipRequest.getDefaultInstance());
        return Stringify.format(response.getCode());
    }

    String dump(String replica) {
        if (updateStub(List.of(replica)) == null)
            return null;

        DumpResponse response = _stub.dump(DumpRequest.getDefaultInstance());
        ResponseCode responseCode = response.getCode();

        if (!responseCode.equals(ResponseCode.OK))
            return Stringify.format(responseCode);

        ClassState classState = response.getClassState();
        return Stringify.format(classState);
    }

    String updateStub(List<String> qualifiers) {
        shutdownChannel();
        String target = _namingServerFrontend.lookup("turmas", qualifiers);
        if (target != null) {
            _channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            _stub = AdminServiceGrpc.newBlockingStub(_channel);
            return "";
        }
        return null;
    }

    void shutdownChannel(){
        if (_channel != null) {
            _channel.shutdownNow();
        }
    }
}
