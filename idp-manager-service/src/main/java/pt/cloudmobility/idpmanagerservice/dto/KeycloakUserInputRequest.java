package pt.cloudmobility.idpmanagerservice.dto;

public class KeycloakUserInputRequest {

    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String email;

    public KeycloakUserInputRequest(String userId, String firstName, String lastName, String role, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "KeycloakUserInputRequest{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
