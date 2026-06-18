package com.buildtogether.dto;
//What it is: A simple "carrier" object. When someone tries to log in, 
//we pack their email + password into this and hand it to the Model. 
//Same exact pattern as ThiranX.
public class LoginRequest {
	private String email;
    private String password;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
