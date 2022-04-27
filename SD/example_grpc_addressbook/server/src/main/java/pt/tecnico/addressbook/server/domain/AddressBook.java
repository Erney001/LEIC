package pt.tecnico.addressbook.server.domain;

import pt.tecnico.addressbook.grpc.AddressBookList;
import pt.tecnico.addressbook.grpc.PersonInfo;
import pt.tecnico.addressbook.grpc.PhoneNumber;
import pt.tecnico.addressbook.grpc.PhoneType;
import pt.tecnico.addressbook.server.domain.exception.DuplicatePersonInfoException;
import pt.tecnico.addressbook.server.domain.exception.UnknownEmailException;
import pt.tecnico.addressbook.server.domain.exception.NoPeopleWithBirthdayException;
import pt.tecnico.addressbook.grpc.PersonInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AddressBook {

    private ConcurrentHashMap<String, Person> people = new ConcurrentHashMap<>();

    public AddressBook() {
    }

    public void addPerson(String name, String email, int phoneNumber, PhoneType type, String birthday) throws DuplicatePersonInfoException {
        if(people.putIfAbsent(email, new Person(name, email, phoneNumber, type, birthday)) != null) {
            throw new DuplicatePersonInfoException(email);
        }
    }

    public AddressBookList proto() {
        return AddressBookList.newBuilder()
                .addAllPeople(people.values().stream().map(Person::proto).collect(Collectors.toList()))
                .build();
    }

    public PersonInfo searchPerson(String email){
        Person person = people.get(email);
        if(person == null){
            throw new UnknownEmailException(email);
        }
        return person.proto();
    }

    public void update(String email, int phoneNumber, PhoneType type){
        Person person = people.get(email);
        person.setPhoneNumber(phoneNumber);
        person.setType(type);
    }

    public List<PhoneNumber> getPhoneNumbersBirthday(String birthday) {
        List<PhoneNumber> phoneNumbers = people.values()
                .stream()
                .filter(person -> person.getBirthday().equals(birthday))
                .map(Person::phoneProto)
                .collect(Collectors.toList());

        if (phoneNumbers.size() == 0) {
            throw new NoPeopleWithBirthdayException(birthday);
        }

        return phoneNumbers;
    }
}
