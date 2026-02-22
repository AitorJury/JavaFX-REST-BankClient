/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.model;

//import lombok.Builder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import crud_project.logic.CustomerRESTClient;
import javafx.beans.property.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entity representing bank customers. Contains personal data, identification
 * data and relational data for accessing customer accounts data.
 *
 * @author Javier Martín Uría
 */
//@Builder
@XmlRootElement
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LongProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty middleInitial;
    private final StringProperty street;
    private final StringProperty city;
    private final StringProperty state;
    private final IntegerProperty zip;
    private final LongProperty phone;
    private final StringProperty email;
    private final StringProperty password;
    private Set<Account> accounts;


    public Customer(Long id, String firstName, String lastName, String middleInitial, String street, String city, String state, Integer zip, Long phone, String email, String password) {
        this.id = new SimpleLongProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.middleInitial = new SimpleStringProperty(middleInitial);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.zip = new SimpleIntegerProperty(zip != null ? zip : 0);
        this.phone = new SimpleLongProperty(phone != null ? phone : 0L);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.accounts = new HashSet<>();
    }


    public Customer() {
        this.id = new SimpleLongProperty();
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.middleInitial = new SimpleStringProperty("");
        this.street = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.state = new SimpleStringProperty("");
        this.zip = new SimpleIntegerProperty();
        this.phone = new SimpleLongProperty();
        this.email = new SimpleStringProperty("name@" + System.currentTimeMillis() + ".com");
        this.password = new SimpleStringProperty("clave$%&");
        this.accounts = new HashSet<>();

    }


    /**
     * @return the accounts
     */

    public Set<Account> getAccounts() {
        return accounts;
    }
    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public Long getId() {
        return id.get();
    }


    public void setId(Long id) {
        this.id.set(id != null ? id : 0L);
    }

    public ObjectProperty<Long> idProperty() {
        return id.asObject();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getMiddleInitial() {
        return middleInitial.get();
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial.set(middleInitial);
    }

    public StringProperty middleInitialProperty() {
        return middleInitial;
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public StringProperty streetProperty() {
        return street;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public StringProperty stateProperty() {
        return state;
    }

    public Integer getZip() {
        return zip.get();
    }

    public void setZip(Integer zip) {
        this.zip.set(zip != null ? zip : 0);
    }

    public IntegerProperty zipProperty() {
        return zip;
    }

    public Long getPhone() {
        return phone.get();
    }

    public void setPhone(Long phone) {
        this.phone.set(phone != null ? phone : 0L);
    }

    public LongProperty phoneProperty() {
        return phone;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }


    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        return this.getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return "Customer[ name=" + getFirstName() + " " + getLastName() + " id=" + getId() + " ]";
    }
}
