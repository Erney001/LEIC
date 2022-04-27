package pt.tecnico.addressbook.server.domain.exception;

public class UnknownEmailException extends IllegalArgumentException {
    private final String email;

    public UnknownEmailException(String email) {
        super("Person with email " + email + " does not exist");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
