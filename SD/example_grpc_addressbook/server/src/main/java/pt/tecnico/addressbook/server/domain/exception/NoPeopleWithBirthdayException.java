package pt.tecnico.addressbook.server.domain.exception;

public class NoPeopleWithBirthdayException extends IllegalArgumentException {
    private final String birthday;

    public NoPeopleWithBirthdayException(String birthday) {
        super("There are no people with birthday " + birthday);
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

}