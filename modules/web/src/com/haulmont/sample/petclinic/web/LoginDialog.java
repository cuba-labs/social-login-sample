package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.app.login.LoginScreen;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.sample.petclinic.service.FacebookService;
import com.haulmont.sample.petclinic.service.FacebookService.FacebookUserData;
import com.haulmont.sample.petclinic.service.GitHubService;
import com.haulmont.sample.petclinic.service.GitHubService.GitHubUserData;
import com.haulmont.sample.petclinic.service.GoogleService;
import com.haulmont.sample.petclinic.web.auth.facebook.FacebookCredentials;
import com.haulmont.sample.petclinic.web.auth.github.GitHubCredentials;
import com.haulmont.sample.petclinic.web.auth.google.GoogleCredentials;
import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Locale;

@Route
@DialogMode(width = "430")
@UiDescriptor("login-dialog.xml")
@UiController("LoginDialog")
public class LoginDialog extends LoginScreen {

    private static final Logger log = LoggerFactory.getLogger(LoginDialog.class);

    private static final String CODE_PARAM = "code";

    @Inject
    private BackgroundWorker backgroundWorker;
    @Inject
    private GlobalConfig globalConfig;

    @Inject
    private GoogleService googleService;
    @Inject
    private FacebookService facebookService;
    @Inject
    private GitHubService githubService;

    private RequestHandler facebookCallBackRequestHandler = this::handleFacebookCallBackRequest;
    private RequestHandler githubCallBackRequestHandler = this::handleGitHubCallBackRequest;
    private RequestHandler googleCallBackRequestHandler = this::handleGoogleCallBackRequest;

    private URI redirectUri;
    private UIAccessor uiAccessor;

    @Subscribe
    private void onExtInit(InitEvent event) {
        redirectUri = Page.getCurrent().getLocation();
        uiAccessor = backgroundWorker.getUIAccessor();
    }

    @Subscribe("googleLogin")
    private void onGoogleLoginClick(Button.ClickEvent event) {
        VaadinSession.getCurrent()
                .addRequestHandler(googleCallBackRequestHandler);

        String loginUrl = googleService.getLoginUrl(globalConfig.getWebAppUrl(),
                GoogleService.OAuth2ResponseType.CODE);

        openUrl(loginUrl);
    }

    @Subscribe("fbLogin")
    private void onFbLoginClick(Button.ClickEvent event) {
        VaadinSession.getCurrent()
                .addRequestHandler(facebookCallBackRequestHandler);

        String loginUrl = facebookService.getLoginUrl(globalConfig.getWebAppUrl(),
                FacebookService.OAuth2ResponseType.CODE);

        openUrl(loginUrl);
    }

    @Subscribe("ghLogin")
    private void onGhLoginClick(Button.ClickEvent event) {
        VaadinSession.getCurrent()
                .addRequestHandler(githubCallBackRequestHandler);

        String loginUrl = githubService.getLoginUrl(globalConfig.getWebAppUrl(),
                GitHubService.OAuth2ResponseType.CODE);

        openUrl(loginUrl);
    }

    public void performLogin() {
        Collection<Screen> openedScreens = screens.getOpenedScreens()
                .getCurrentBreadcrumbs();

        Screen currentScreen = !openedScreens.isEmpty()
                ? openedScreens.iterator().next()
                : null;

        login();

        if (connection.isAuthenticated()) {
            close(WINDOW_CLOSE_ACTION);

            if (currentScreen != null) {
                screens.create(currentScreen.getClass(), OpenMode.NEW_TAB)
                        .show();
            }
        }
    }

    private void openUrl(String loginUrl) {
        Page.getCurrent()
                .setLocation(loginUrl);
    }

    private boolean handleGoogleCallBackRequest(VaadinSession session, VaadinRequest request,
                                                VaadinResponse response) throws IOException {
        if (request.getParameter(CODE_PARAM) == null) {
            return false;
        }

        uiAccessor.accessSynchronously(() -> {
            try {
                GoogleCredentials credentials = getGoogleCredentials(request);

                app.getConnection()
                        .login(credentials);
            } catch (Exception e) {
                log.error("Unable to login using Google", e);
            } finally {
                session.removeRequestHandler(googleCallBackRequestHandler);
            }
        });

        ((VaadinServletResponse) response).getHttpServletResponse().
                sendRedirect(ControllerUtils.getLocationWithoutParams(redirectUri));

        return true;
    }

    private boolean handleFacebookCallBackRequest(VaadinSession session, VaadinRequest request,
                                                  VaadinResponse response) throws IOException {
        if (request.getParameter(CODE_PARAM) == null) {
            return false;
        }

        uiAccessor.accessSynchronously(() -> {
            try {
                FacebookCredentials credentials = getFacebookCredentials(request);

                app.getConnection()
                        .login(credentials);
            } catch (Exception e) {
                log.error("Unable to login using Facebook", e);
            } finally {
                session.removeRequestHandler(facebookCallBackRequestHandler);
            }
        });

        ((VaadinServletResponse) response).getHttpServletResponse().
                sendRedirect(ControllerUtils.getLocationWithoutParams(redirectUri));

        return true;
    }

    private boolean handleGitHubCallBackRequest(VaadinSession session, VaadinRequest request,
                                                VaadinResponse response) throws IOException {
        if (request.getParameter(CODE_PARAM) == null) {
            return false;
        }

        uiAccessor.accessSynchronously(() -> {
            try {
                GitHubCredentials credentials = getGitHubCredentials(request);

                app.getConnection()
                        .login(credentials);
            } catch (Exception e) {
                log.error("Unable to login using GitHub", e);
            } finally {
                session.removeRequestHandler(githubCallBackRequestHandler);
            }
        });

        ((VaadinServletResponse) response).getHttpServletResponse().
                sendRedirect(ControllerUtils.getLocationWithoutParams(redirectUri));

        return true;
    }

    private GoogleCredentials getGoogleCredentials(VaadinRequest request) {
        String code = request.getParameter(CODE_PARAM);

        GoogleService.GoogleUserData userData = googleService.getUserData(
                globalConfig.getWebAppUrl(), code);

        Locale defaultLocale = messages.getTools()
                .getDefaultLocale();

        return new GoogleCredentials(userData, defaultLocale);
    }

    private FacebookCredentials getFacebookCredentials(VaadinRequest request) {
        String code = request.getParameter(CODE_PARAM);

        FacebookUserData userData = facebookService.getUserData(
                globalConfig.getWebAppUrl(), code);

        Locale defaultLocale = messages.getTools()
                .getDefaultLocale();

        return new FacebookCredentials(userData, defaultLocale);
    }

    private GitHubCredentials getGitHubCredentials(VaadinRequest request) {
        String code = request.getParameter(CODE_PARAM);

        GitHubUserData userData = githubService.getUserData(
                globalConfig.getWebAppUrl(), code);

        Locale defaultLocale = messages.getTools()
                .getDefaultLocale();

        return new GitHubCredentials(userData, defaultLocale);
    }

    // VK Auth
    // private RequestHandler vkCallBackRequestHandler = this::handleVkCallBackRequest;

    // VK Auth
    /*private void onVkLoginClick(Button.ClickEvent event) {
        VaadinSession.getCurrent()
                .addRequestHandler(vkCallBackRequestHandler);

        this.redirectUri = Page.getCurrent().getLocation();

        String loginUrl = vkService.getLoginUrl(globalConfig.getWebAppUrl(),
                VkService.OAuth2ResponseType.CODE);
        Page.getCurrent()
                .setLocation(loginUrl);
    }*/

    // VK Auth
    /*private boolean handleVkCallBackRequest(VaadinSession session, VaadinRequest request,
                                            VaadinResponse response) throws IOException {
        if (request.getParameter(CODE_PARAM) == null) {
            return false;
        }

        uiAccessor.accessSynchronously(() -> {
            try {
                VkCredentials credentials = getVkCredentials(request);

                app.getConnection()
                        .login(credentials);
            } catch (Exception e) {
                log.error("Unable to login using Facebook", e);
            } finally {
                session.removeRequestHandler(facebookCallBackRequestHandler);
            }
        });

        ((VaadinServletResponse) response).getHttpServletResponse().
                sendRedirect(ControllerUtils.getLocationWithoutParams(redirectUri));

        return true;
    }*/

    // VK Auth
    /*private VkCredentials getVkCredentials(VaadinRequest request) {
        String code = request.getParameter(CODE_PARAM);

        VkUserData userData = vkService
                .getUserData(globalConfig.getWebAppUrl(), code);

        Locale defaultLocale = messages.getTools()
                .getDefaultLocale();

        return new VkCredentials(userData, defaultLocale);
    }*/
}
