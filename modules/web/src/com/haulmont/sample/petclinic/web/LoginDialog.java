package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.app.login.LoginScreen;
import com.haulmont.sample.petclinic.auth.SocialService;
import com.haulmont.sample.petclinic.service.SocialLoginService;
import com.haulmont.sample.petclinic.web.auth.SocialServiceCallbackHandler;
import com.vaadin.server.Page;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinSession;

import javax.inject.Inject;
import java.util.Collection;

@Route
@DialogMode(width = "430")
@UiDescriptor("login-dialog.xml")
@UiController("LoginDialog")
public class LoginDialog extends LoginScreen {

    @Inject
    private SocialLoginService socialLoginService;

    @Subscribe("googleLogin")
    private void onGoogleLoginClick(Button.ClickEvent event) {
        performSocialLogin(SocialService.GOOGLE);
    }

    @Subscribe("fbLogin")
    private void onFbLoginClick(Button.ClickEvent event) {
        performSocialLogin(SocialService.FACEBOOK);
    }

    @Subscribe("ghLogin")
    private void onGhLoginClick(Button.ClickEvent event) {
        performSocialLogin(SocialService.GITHUB);
    }

    private void performSocialLogin(SocialService socialService) {
        VaadinSession.getCurrent()
                .addRequestHandler(getCallbackHandler(socialService));

        String loginUrl = socialLoginService.getLoginUrl(socialService);

        close(WINDOW_CLOSE_ACTION);

        Page.getCurrent()
                .setLocation(loginUrl);
    }

    private RequestHandler getCallbackHandler(SocialService socialService) {
        return getBeanLocator()
                .getPrototype(SocialServiceCallbackHandler.NAME, socialService);
    }

    public void performDefaultLogin() {
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
}
