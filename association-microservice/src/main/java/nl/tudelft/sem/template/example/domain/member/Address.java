package nl.tudelft.sem.template.example.domain.member;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Address {
    public final transient String addressName;

    public Address(String address) {
        this.addressName = address;
    }

    @Override
    public String toString() {
        return addressName;
    }
}
