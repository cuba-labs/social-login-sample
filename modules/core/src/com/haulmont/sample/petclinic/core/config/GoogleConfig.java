package com.haulmont.sample.petclinic.core.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface GoogleConfig extends Config, SocialServiceConfig {

    @Property("google.clientId")
    String getClientId();

    @Property("google.clientSecret")
    String getClientSecret();

    @Default("id,name,email")
    @Property("google.userDataFields")
    String getUserDataFields();
}