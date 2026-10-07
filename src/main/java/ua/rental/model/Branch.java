package ua.rental.model;

import java.util.Objects;

import ua.rental.util.RentalUtils;

public class Branch extends ua.common.BaseEntity {
    private String name;
    private String location;

    public Branch(String name, String location) {
        super();
        RentalUtils.requireNotEmpty(name, "branch name");
        RentalUtils.requireNotEmpty(location, "location name");
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Branch branch = (Branch) o;
        return name.equals(branch.name);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Class: " + getClass().getSimpleName() + "\nname: " + name + "\nlocation: " + location
            + "\ncreatedAt: " + createdAt;
    }
}
