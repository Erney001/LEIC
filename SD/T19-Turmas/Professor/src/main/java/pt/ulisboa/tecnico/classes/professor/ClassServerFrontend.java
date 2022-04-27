package pt.ulisboa.tecnico.classes.professor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ClassState;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;

import java.util.ArrayList;
import java.util.List;

public class ClassServerFrontend {
    private final NamingServerFrontend _namingServerFrontend;
    private ManagedChannel _channel;
    private ProfessorServiceGrpc.ProfessorServiceBlockingStub _stub;
    private List<String> _serviceServers;
    private int _numberOfServers = 0;

    ClassServerFrontend(NamingServerFrontend namingServerFrontend) {
        _namingServerFrontend = namingServerFrontend;
        _serviceServers = new ArrayList<>();
    }

    String openEnrollments(int capacity) {
        updateStub(List.of("P"));
        OpenEnrollmentsRequest request = OpenEnrollmentsRequest.newBuilder().setCapacity(capacity).build();
        ResponseCode responseCode = _stub.openEnrollments(request).getCode();
        return Stringify.format(responseCode);
    }

    String closeEnrollments() {
        updateStub(List.of("P"));
        CloseEnrollmentsRequest request = CloseEnrollmentsRequest.getDefaultInstance();
        ResponseCode responseCode = _stub.closeEnrollments(request).getCode();
        return Stringify.format(responseCode);
    }

    String listClass() {
        ResponseCode responseCode;
        ClassState classState;
        int attempts = 0;

        do {
            updateStub(List.of("P", "S"));
            ListClassRequest request = ListClassRequest.getDefaultInstance();
            ListClassResponse response = _stub.listClass(request);
            responseCode = response.getCode();
            classState = response.getClassState();

        } while (responseCode.equals(ResponseCode.INACTIVE_SERVER) && ++attempts < _numberOfServers);

        _serviceServers.clear();
        if (!responseCode.equals(ResponseCode.OK))
            return Stringify.format(responseCode);

        return Stringify.format(classState);
    }

    String cancelEnrollment(String studentId) {
        ResponseCode responseCode;
        int attempts = 0;

        do {
            updateStub(List.of("P"));
            CancelEnrollmentRequest request = CancelEnrollmentRequest.newBuilder().setStudentId(studentId).build();
            responseCode = _stub.cancelEnrollment(request).getCode();

        } while (responseCode.equals(ResponseCode.INACTIVE_SERVER) && ++attempts < _numberOfServers);

        _serviceServers.clear();
        return Stringify.format(responseCode);
    }

    void updateStub(List<String> qualifiers) {
        shutdownChannel();

        if (_serviceServers.size() == 0) {
            _serviceServers = _namingServerFrontend.lookup("turmas", qualifiers);
            _numberOfServers = _serviceServers.size();
        }

        _channel = ManagedChannelBuilder.forTarget(_serviceServers.get(0)).usePlaintext().build();
        _stub = ProfessorServiceGrpc.newBlockingStub(_channel);

        _serviceServers.remove(0);
    }

    void shutdownChannel() {
        if (_channel != null) {
            _channel.shutdownNow();
        }
    }
}
