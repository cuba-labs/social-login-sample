package com.haulmont.sample.petclinic.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.sample.petclinic.config.SocialRegistrationConfig;
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
    public User findOrRegisterGoogleUser(String googleId, String name, String email) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SocialUser> query = em
                .createQuery("select u from sec$User u where u.googleId = :googleId",
                        SocialUser.class);
        query.setParameter("googleId", googleId);
        query.setViewName(View.LOCAL);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setGoogleId(googleId);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setEmail(email);
        user.setLogin(email);

        em.persist(user);

        return user;
    }

    @Override
    @Transactional
    public User findOrRegisterFacebookUser(String facebookId, String name, String email) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SocialUser> query = em.createQuery("select u from sec$User u where u.facebookId = :facebookId",
                SocialUser.class);
        query.setParameter("facebookId", facebookId);
        query.setViewName(View.LOCAL);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setFacebookId(facebookId);
        user.setEmail(email);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(email);

        em.persist(user);

        return user;
    }

    @Override
    @Transactional
    public User findOrRegisterGitHubUser(String githubId, String name, String login) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SocialUser> query = em
                .createQuery("select u from sec$User u where u.githubId = :githubId",
                        SocialUser.class);
        query.setParameter("githubId", githubId);
        query.setViewName(View.LOCAL);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setGithubId(githubId);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(login);

        if (isEmail(login)) {
            user.setEmail(login);
        }

        em.persist(user);

        return user;
    }

    @Override
    @Transactional
    public User findOrRegisterVkUser(String vkId, String name, String screenName) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SocialUser> query = em
                .createQuery("select u from sec$User u where u.vkId = :vkId",
                        SocialUser.class);
        query.setParameter("vkId", vkId);
        query.setViewName(View.LOCAL);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setVkId(vkId);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(screenName);

        em.persist(user);

        return user;
    }

    private boolean isEmail(String s) {
        return EMAIL_PATTERN.matcher(s).matches();
    }
}