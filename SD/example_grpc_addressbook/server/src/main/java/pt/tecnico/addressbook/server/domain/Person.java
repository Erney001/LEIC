package pt.tecnico.addressbook.server.domain;

import pt.tecnico.addressbook.grpc.PersonInfo;
import pt.tecnico.addressbook.grpc.PhoneNumber;
import pt.tecnico.addressbook.grpc.PhoneType;

public class Person {
    private String name;
    private String email;
    private int phoneNumber;
    private PhoneType type;
    private String birthday;

    public Person(String name, String email, int phoneNumber, PhoneType type, String birthday) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.birthday = birthday;
    }

    public String getBirthday(){
        return birthday;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public PersonInfo proto() {
        PhoneNumber phone = PhoneNumber.newBuilder().setNumber(phoneNumber).setType(type).build();
        return PersonInfo.newBuilder().setName(name).setEmail(email).setPhone(phone).setBirthday(birthday).build();
    }

    public PhoneNumber phoneProto() {
        return PhoneNumber.newBuilder().setNumber(phoneNumber).setType(type).build();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", type=" + type +
                '}';
    }
}
