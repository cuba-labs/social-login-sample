package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.Connection;
import com.haulmont.cuba.web.app.login.LoginScreen;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.sample.petclinic.service.FacebookService;
import com.haulmont.sample.petclinic.service.FacebookService.FacebookUserData;
import com.haulmont.sample.petclinic.service.SocialRegistrationService;
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

    private final Logger log = LoggerFactory.getLogger(LoginDialog.class);

    private RequestHandler facebookCallBackRequestHandler =
            this::handleFacebookCallBackRequest;

    @Inject
    private BackgroundWorker backgroundWorker;
    @Inject
    private FacebookService facebookService;
    @Inject
    private SocialRegistrationService socialRegistrationService;
    @Inject
    private GlobalConfig globalConfig;

    private URI redirectUri;
    private UIAccessor uiAccessor;

    @Subscribe
    private void onExtInit(InitEvent event) {
        uiAccessor = backgroundWorker.getUIAccessor();
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

    @Subscribe("fbLogin")
    private void onFbLoginClick(Button.ClickEvent event) {
        VaadinSession.getCurrent()
                .addRequestHandler(facebookCallBackRequestHandler);

        this.redirectUri = Page.getCurrent().getLocation();

        String loginUrl = facebookService.getLoginUrl(globalConfig.getWebAppUrl(), FacebookService.OAuth2ResponseType.CODE);
        Page.getCurrent()
                .setLocation(loginUrl);
    }

    @Subscribe("twLogin")
    private void onTwLoginClick(Button.ClickEvent event) {

    }

    @Subscribe("ghLogin")
    private void onGhLoginClick(Button.ClickEvent event) {

    }

    public boolean handleFacebookCallBackRequest(VaadinSession session, VaadinRequest request,
                                                 VaadinResponse response) throws IOException {
        if (request.getParameter("code") != null) {
            uiAccessor.accessSynchronously(() -> {
                try {
                    String code = request.getParameter("code");

                    FacebookUserData userData = facebookService
                            .getUserData(globalConfig.getWebAppUrl(), code);

                    User user = socialRegistrationService.findOrRegisterUser(
                            userData.getId(), userData.getEmail(), userData.getName());

                    Connection connection = app.getConnection();

                    Locale defaultLocale = messages.getTools().getDefaultLocale();
                    connection.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
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

        return false;
    }
}
