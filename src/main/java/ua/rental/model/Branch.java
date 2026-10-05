package ua.rental.model;

import java.util.Objects;

public class Branch extends ua.common.BaseEntity {
    private String name;
    private String location;

    public Branch(String name, String location) {
        super();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty, got: " + name);
        }
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Branch location cannot be null or empty, got: " + location);
        }
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
        if (!(o instanceof Branch branch)) return false;
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
