package pt.cloudmobility.userservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(value = "\"user\"")
public class User {

    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String identificationNumber;
    private InternalRole role;
    private String email;

    public User() {
    }

    public User(Integer id, String firstName, String lastName, String identificationNumber, InternalRole role, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identificationNumber = identificationNumber;
        this.role = role;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public InternalRole getRole() {
        return role;
    }

    public void setRole(InternalRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(identificationNumber, user.identificationNumber) && role == user.role && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, identificationNumber, role, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                '}';
    }
}
