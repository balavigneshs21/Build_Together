package com.buildtogether.features.home;

import com.buildtogether.dto.User;

public class HomeModel {

    private final HomeView homeView;

    public HomeModel(HomeView homeView) {
        this.homeView = homeView;
    }

    public void init(User user) {
        if (user.getRole() == User.Role.DEVELOPER) {
            homeView.showDeveloperMenu();
        } else if (user.getRole() == User.Role.INVESTOR) {
            homeView.showInvestorMenu();
        } else {
            homeView.showUnauthorized();
        }
    }
}