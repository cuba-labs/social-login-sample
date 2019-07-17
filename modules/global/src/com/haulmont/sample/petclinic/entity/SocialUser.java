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

    @Column(name = "GOOGLE_ID")
    protected String googleId;

    @Column(name = "GITHUB_ID")
    protected String githubId;

    @Column(name = "VK_ID")
    protected String vkId;

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getVkId() {
        return vkId;
    }

    public void setVkId(String vkId) {
        this.vkId = vkId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}