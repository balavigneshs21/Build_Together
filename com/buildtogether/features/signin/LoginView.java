package com.buildtogether.features.signin;

import com.buildtogether.dto.LoginRequest;
import com.buildtogether.dto.User;
import com.buildtogether.features.home.HomeView;
import com.buildtogether.features.signup.SignUpView;
import com.buildtogether.util.ConsoleInput;

import java.util.Scanner;

public class LoginView {

    private final LoginModel loginModel;
    private final Scanner scanner;
    private boolean authenticated;

    public LoginView() {
        this.loginModel = new LoginModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.authenticated = false;
    }

    public void init() {
        System.out.println();
        System.out.println("Login to BuildTogether");

        while (!authenticated) {
            promptAndAuthenticate();
            if (authenticated) return;
            if (!promptPostFailureAction()) return;
        }
    }

    private void promptAndAuthenticate() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        loginModel.authenticate(request);
    }

    private boolean promptPostFailureAction() {
        while (true) {
            System.out.println();
            System.out.println("1. Retry");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return true;
                case "2":
                    new SignUpView().init();
                    return false;
                case "3":
                    System.out.println("Goodbye!");
                    System.exit(0);
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    void onLoginSuccess(User user) {
        authenticated = true;
        System.out.println("Welcome back, " + user.getName() + "!");
        new HomeView(user).init();
    }

    void onLoginFailed(String message) {
        System.out.println(message);
    }
}