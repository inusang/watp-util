package org.watp.util.entity;

import java.io.Serializable;

public class PersonStay implements Serializable {
    private String id;
    private String city;
    private String village;

    public PersonStay() {
    }

    public PersonStay(String id, String city, String village) {
        this.id = id;
        this.city = city;
        this.village = village;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }
}
