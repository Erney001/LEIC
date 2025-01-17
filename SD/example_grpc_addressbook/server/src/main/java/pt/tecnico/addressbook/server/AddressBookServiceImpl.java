package pt.tecnico.addressbook.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.addressbook.grpc.*;
import pt.tecnico.addressbook.grpc.AddressBookServiceGrpc.AddressBookServiceImplBase;
import pt.tecnico.addressbook.server.domain.AddressBook;
import pt.tecnico.addressbook.server.domain.exception.DuplicatePersonInfoException;
import pt.tecnico.addressbook.server.domain.exception.NoPeopleWithBirthdayException;
import pt.tecnico.addressbook.server.domain.exception.UnknownEmailException;

import java.util.List;

import static io.grpc.Status.INVALID_ARGUMENT;

public class AddressBookServiceImpl extends AddressBookServiceImplBase {

    AddressBook addressBook = new AddressBook();

    @Override
    public void listPeople(ListPeopleRequest request, StreamObserver<AddressBookList> responseObserver) {
        AddressBookList response = addressBook.proto();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addPerson(PersonInfo request, StreamObserver<AddPersonResponse> responseObserver) {
        try {
            addressBook.addPerson(request.getName(), request.getEmail(), request.getPhone().getNumber(), request.getPhone().getType(), request.getBirthday());
            AddPersonResponse response = AddPersonResponse.getDefaultInstance();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        catch (DuplicatePersonInfoException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void searchPerson(SearchPersonRequest request, StreamObserver<PersonInfo> responseObserver) {
        try{
            PersonInfo response = addressBook.searchPerson(request.getEmail());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch(UnknownEmailException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void update(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
        addressBook.update(request.getEmail(), request.getPhone().getNumber(), request.getPhone().getType());
        UpdateResponse response = UpdateResponse.getDefaultInstance();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listAll(ListAllRequest request, StreamObserver<ListAllResponse> responseObserver) {
        try {
            ListAllResponse response = ListAllResponse.newBuilder()
                    .addAllPhoneNumber(addressBook.getPhoneNumbersBirthday(request.getBirthday()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoPeopleWithBirthdayException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
