package com.haulmont.sample.petclinic.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.sample.petclinic.auth.SocialService;
import com.haulmont.sample.petclinic.core.config.SocialRegistrationConfig;
import com.haulmont.sample.petclinic.entity.SocialUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.regex.Pattern;

@Service(SocialRegistrationService.NAME)
public class SocialRegistrationServiceBean implements SocialRegistrationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[^@]+@[^.]+\\..+");

    @Inject
    private Metadata metadata;
    @Inject
    private Persistence persistence;
    @Inject
    private Configuration configuration;

    @Override
    @Transactional
    public User findOrRegisterUser(String socialServiceId, String login, String name, SocialService socialService) {
        // Find existing user
        TypedQuery<SocialUser> query = getUserQuery(socialService, socialServiceId);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        EntityManager em = persistence.getEntityManager();
        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setLogin(login);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);

        if (isEmail(login)) {
            user.setEmail(login);
        }

        switch (socialService) {
            case GOOGLE:
                user.setGoogleId(socialServiceId);
                break;
            case FACEBOOK:
                user.setFacebookId(socialServiceId);
                break;
            case GITHUB:
                user.setGithubId(socialServiceId);
                break;
        }

        em.persist(user);

        return user;
    }

    private TypedQuery<SocialUser> getUserQuery(SocialService socialService, String socialServiceId) {
        EntityManager em = persistence.getEntityManager();

        String socialIdParam = getSocialIdParamName(socialService);

        TypedQuery<SocialUser> query = em.createQuery("select u from sec$User u where " +
                        String.format("u.%s = :%s", socialIdParam, socialIdParam),
                SocialUser.class);
        query.setParameter(socialIdParam, socialServiceId);
        query.setViewName(View.LOCAL);

        return query;
    }

    private String getSocialIdParamName(SocialService socialService) {
        switch (socialService) {
            case GOOGLE:
                return "googleId";
            case FACEBOOK:
                return "facebookId";
            case GITHUB:
                return "githubId";
        }
        throw new IllegalArgumentException("No social id param found for service: " + socialService);
    }

    private boolean isEmail(String s) {
        return EMAIL_PATTERN.matcher(s).matches();
    }
}