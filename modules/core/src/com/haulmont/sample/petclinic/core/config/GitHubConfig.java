package com.haulmont.sample.petclinic.core.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface GitHubConfig extends Config, SocialServiceConfig {

    @Property("github.clientId")
    String getClientId();

    @Property("github.clientSecret")
    String getClientSecret();

    @Default("")
    @Property("github.userDataFields")
    String getUserDataFields();
}