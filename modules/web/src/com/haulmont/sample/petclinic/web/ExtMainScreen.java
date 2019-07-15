package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.mainwindow.UserActionsButton;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.web.app.main.MainScreen;

import javax.inject.Inject;

@UiController("main")
@UiDescriptor("ext-main-screen.xml")
public class ExtMainScreen extends MainScreen {

    @Inject
    private Screens screens;

    @Subscribe
    private void onInit(InitEvent event) {
        UserActionsButton userActionsButton = (UserActionsButton) getWindow()
                .getComponent("userActionsButton");

        if (userActionsButton != null) {
            userActionsButton.setLoginHandler(() ->
                    screens.create(LoginDialog.class, OpenMode.DIALOG)
                            .show());
        }
    }
}