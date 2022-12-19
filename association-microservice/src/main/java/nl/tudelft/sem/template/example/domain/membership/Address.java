package nl.tudelft.sem.template.example.domain.membership;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Address {
    public final transient String addressName;

    public Address(String address) {
        this.addressName = address;
    }

    /**get the underlying string.
     *
     * @return the underlying string
     */
    @Override
    public String toString() {
        return addressName;
    }
}
