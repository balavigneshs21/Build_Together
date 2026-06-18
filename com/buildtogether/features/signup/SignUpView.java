package com.buildtogether.features.signup;

import com.buildtogether.dto.User;
import com.buildtogether.features.signin.LoginView;
import com.buildtogether.util.ConsoleInput;

import java.util.Scanner;

public class SignUpView {

    private final SignUpModel signUpModel;
    private final Scanner scanner;

    public SignUpView() {
        this.signUpModel = new SignUpModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        System.out.println();
        System.out.println("Create your BuildTogether account");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.println("Select your role:");
        System.out.println("1. Developer");
        System.out.println("2. Investor");
        System.out.print("Choose (1 or 2): ");
        String roleChoice = scanner.nextLine().trim();

        signUpModel.registerUser(name, email, password, roleChoice);
    }

    void onSignUpSuccess(User user) {
        System.out.println("Account created successfully! Welcome, " + user.getName());
        System.out.println("Please sign in to continue.");
        new LoginView().init();
        
    }

    void onSignUpFailed(String message) {
        System.out.println("Sign up failed: " + message);
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}