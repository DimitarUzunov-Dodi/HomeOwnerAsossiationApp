package nl.tudelft.sem.template.association.domain.location;

public class Address extends Location {
    private String street;
    private String houseNumber;
    private String postalCode;

    /**constructor.
     *
     * @param location parent object
     * @param street the street of the house
     * @param houseNumber house number
     * @param postalCode local postal code
     */
    public Address(Location location, String street, String houseNumber, String postalCode) {
        super(location);
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Location getLocation() {
        return new Location(super.getCountry(), super.getCity());
    }
}
