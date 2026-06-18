package com.buildtogether.features.signup;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.regex.Pattern;

 class SignUpModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final SignUpView signUpView;

    SignUpModel(SignUpView signUpView) {
        this.signUpView = signUpView;
    }

    void registerUser(String name, String email, String password, String roleChoice) {

        if (name == null || name.isEmpty()) {
            signUpView.onSignUpFailed("Name cannot be empty");
            return;
        }

        if (email == null || email.isEmpty()) {
            signUpView.onSignUpFailed("Email cannot be empty");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            signUpView.onSignUpFailed("Enter a valid email address");
            return;
        }

        if (BuildTogetherDB.getInstance().isEmailTaken(email)) {
            signUpView.onSignUpFailed("This email is already registered");
            return;
        }

        if (password == null || password.length() < 6) {
            signUpView.onSignUpFailed("Password must be at least 6 characters");
            return;
        }

        User.Role role;
        if (roleChoice.equals("1")) {
            role = User.Role.DEVELOPER;
        } else if (roleChoice.equals("2")) {
            role = User.Role.INVESTOR;
        } else {
            signUpView.onSignUpFailed("Invalid role selection");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        User saved = BuildTogetherDB.getInstance().addUser(user);
        if (saved != null) {
            signUpView.onSignUpSuccess(saved);
        } else {
            signUpView.onSignUpFailed("Something went wrong. Please try again.");
        }
    }
}