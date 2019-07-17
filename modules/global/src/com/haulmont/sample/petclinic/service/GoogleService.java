package com.haulmont.sample.petclinic.service;

import java.io.Serializable;

public interface GoogleService {

    String NAME = "petclinic_GoogleService";

    String getLoginUrl(String appUrl, OAuth2ResponseType responseType);

    GoogleUserData getUserData(String appUrl, String code);

    enum OAuth2ResponseType {

        CODE("code");

        private final String id;

        OAuth2ResponseType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    class GoogleUserData implements Serializable {

        private String id;
        private String name;
        private String email;

        public GoogleUserData(String id, String name, String email) {
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
            return "GoogleUserData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}