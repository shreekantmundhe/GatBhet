package com.gatbhet.model;

/**
 * Created by ADMINIBM on 5/6/2016.
 */
public class User {
    String usr_ref_id;
    String name;
    String photo;
    String city;
    String type;
    String group;

    public String getUsr_ref_id() {
        return usr_ref_id;
    }

    public void setUsr_ref_id(String usr_ref_id) {
        this.usr_ref_id = usr_ref_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
