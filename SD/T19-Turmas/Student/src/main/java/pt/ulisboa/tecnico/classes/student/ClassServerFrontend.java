package pt.ulisboa.tecnico.classes.student;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ClassState;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.Student;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;

import java.util.ArrayList;
import java.util.List;

public class ClassServerFrontend {
    private final NamingServerFrontend _namingServerFrontend;
    private ManagedChannel _channel;
    private StudentServiceGrpc.StudentServiceBlockingStub _stub;
    private List<String> _serviceServers;
    private int _numberOfServers = 0;

    ClassServerFrontend(NamingServerFrontend namingServerFrontend) {
        _namingServerFrontend = namingServerFrontend;
        _serviceServers = new ArrayList<>();
    }

    String list() {
        ResponseCode responseCode;
        ClassState classState;
        int attempts = 0;

        do {
            updateStub(List.of("P", "S"));
            ListClassResponse response = _stub.listClass(ListClassRequest.getDefaultInstance());

            responseCode = response.getCode();
            classState = response.getClassState();

        } while (responseCode.equals(ResponseCode.INACTIVE_SERVER) && ++attempts < _numberOfServers);

        _serviceServers.clear();

        if (!responseCode.equals(ResponseCode.OK))
            return Stringify.format(responseCode);

        return Stringify.format(classState);
    }

    String enroll(String studentId, String studentName) {
        ResponseCode responseCode;
        int attempts = 0;

        do {
            updateStub(List.of("P", "S"));
            Student student = Student.newBuilder()
                    .setStudentId(studentId)
                    .setStudentName(studentName)
                    .build();

            EnrollRequest request = EnrollRequest.newBuilder()
                    .setStudent(student)
                    .build();

            responseCode = _stub.enroll(request).getCode();

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
        _stub = StudentServiceGrpc.newBlockingStub(_channel);

        _serviceServers.remove(0);
    }

    void shutdownChannel() {
        if (_channel != null) {
            _channel.shutdownNow();
        }
    }
}
