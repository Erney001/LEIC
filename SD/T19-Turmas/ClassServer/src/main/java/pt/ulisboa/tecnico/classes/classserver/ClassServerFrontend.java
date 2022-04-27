package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.*;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;

import java.util.ArrayList;
import java.util.List;

public class ClassServerFrontend {
    private final NamingServerFrontend _namingServerFrontend;
    private ManagedChannel _channel;
    private ClassServerServiceGrpc.ClassServerServiceBlockingStub _classServerStub;
    private String _target;

    ClassServerFrontend(NamingServerFrontend namingServerFrontend, String target){
        _namingServerFrontend = namingServerFrontend;
        _target = target;
    }

    List<ResponseCode> propagateState(ClassState state) {
        List<String> serviceServers = new ArrayList<>(_namingServerFrontend.lookup("turmas", List.of("P", "S")));

        serviceServers.remove(_target);

        if (serviceServers.size() == 0) {
            return null;
        }

        PropagateStateRequest request = PropagateStateRequest.newBuilder().setClassState(state.toProto()).build();

        List<ResponseCode> codes = new ArrayList<>();

        for (String target: serviceServers) {
            _channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            _classServerStub = ClassServerServiceGrpc.newBlockingStub(_channel);
            codes.add(_classServerStub.propagateState(request).getCode());
            shutdown();
        }

        return codes;
    }

    void shutdown() {
        if (_channel != null) {
            _channel.shutdownNow();
        }
    }
}
