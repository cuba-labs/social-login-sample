package com.haulmont.sample.petclinic.service;

import java.io.Serializable;

public interface FacebookService {

    String NAME = "petclinic_FacebookService";

    /**
     * Returns a URL that can be used to authenticate
     *
     * @param appUrl       URL that will be used as redirect URL
     * @param responseType defines Facebook API response type
     */
    String getLoginUrl(String appUrl, OAuth2ResponseType responseType);

    /**
     * Loads user data from Facebook.
     *
     * @param appUrl URL that will be used as redirect URL
     * @param code   authentication code
     */
    FacebookUserData getUserData(String appUrl, String code);

    enum OAuth2ResponseType {
        CODE("code"),
        TOKEN("token"),
        CODE_TOKEN("code%20token");

        private final String id;

        OAuth2ResponseType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    class FacebookUserData implements Serializable {

        private String id;
        private String name;
        private String email;

        public FacebookUserData(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "FacebookUserData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}