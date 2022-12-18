package nl.tudelft.sem.template.example.domain.membership;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Address {
    public final transient String address;

    public Address(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address;
    }
}
