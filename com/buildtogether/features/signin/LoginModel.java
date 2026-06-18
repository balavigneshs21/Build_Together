package com.buildtogether.features.signin;

import com.buildtogether.dto.LoginRequest;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.regex.Pattern;

public class LoginModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final LoginView loginView;

    LoginModel(LoginView loginView) {
        this.loginView = loginView;
    }

    void authenticate(LoginRequest request) {
        if (request == null) {
            loginView.onLoginFailed("Invalid request");
            return;
        }

        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || email.isEmpty()) {
            loginView.onLoginFailed("Email cannot be empty");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            loginView.onLoginFailed("Enter a valid email address");
            return;
        }

        if (password == null || password.isEmpty()) {
            loginView.onLoginFailed("Password cannot be empty");
            return;
        }

        User user = BuildTogetherDB.getInstance().authenticateUser(email, password);

        if (user == null) {
            loginView.onLoginFailed("Invalid email or password");
            return;
        }

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            loginView.onLoginFailed("Your account is inactive. Please contact support.");
            return;
        }

        loginView.onLoginSuccess(user);
    }
}