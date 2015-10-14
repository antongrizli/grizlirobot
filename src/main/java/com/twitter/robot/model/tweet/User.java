package com.twitter.robot.model.tweet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Антон on 01.10.2015.
 */
@XmlType(propOrder = {"idUser", "nameUser", "screenNameUser", "location"})
public class User {
    private Long idUser;
    private String nameUser;
    private String screenNameUser;
    private String location;

    @XmlElement(name = "id")
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    @XmlElement(name = "name")
    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
    @XmlElement(name = "screen_name")
    public String getScreenNameUser() {
        return screenNameUser;
    }

    public void setScreenNameUser(String screenNameUser) {
        this.screenNameUser = screenNameUser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", nameUser='" + nameUser + '\'' +
                ", screenNameUser='" + screenNameUser + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
