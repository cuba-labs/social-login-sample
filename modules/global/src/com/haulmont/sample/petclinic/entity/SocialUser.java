package com.haulmont.sample.petclinic.entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;

@Extends(User.class)
@Entity(name = "petclinic_SocialUser")
public class SocialUser extends User {
    private static final long serialVersionUID = 6206529748763945044L;

    @Column(name = "FACEBOOK_ID")
    protected String facebookId;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}