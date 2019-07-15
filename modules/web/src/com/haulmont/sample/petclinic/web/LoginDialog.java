package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.web.app.login.LoginScreen;

import java.util.Collection;

@Route
@DialogMode(width = "430")
@UiDescriptor("login-dialog.xml")
@UiController("LoginDialog")
public class LoginDialog extends LoginScreen {

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
}
