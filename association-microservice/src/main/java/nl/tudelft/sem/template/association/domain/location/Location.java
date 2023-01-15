package nl.tudelft.sem.template.association.domain.location;

import java.util.Objects;

public class Location {
    private String country;
    private String city;

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Location(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public Location(Location location){
        this.country=location.country;
        this.city=location.city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return country.equals(location.country) && city.equals(location.city);
    }

}
