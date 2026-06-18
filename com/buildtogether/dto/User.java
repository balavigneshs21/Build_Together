package com.buildtogether.dto;

public class User {
	   	private Long id;
	    private String name;
	    private String email;
	    private String password;
	    private Role role;
	    private UserStatus status;
	    private Long createdAt;

	    public enum Role {
	        DEVELOPER, INVESTOR
	    }

	    public enum UserStatus {
	        ACTIVE, INACTIVE
	    }

	    public User() {
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
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

	    public Role getRole() {
	        return role;
	    }

	    public void setRole(Role role) {
	        this.role = role;
	    }

	    public UserStatus getStatus() {
	        return status;
	    }

	    public void setStatus(UserStatus status) {
	        this.status = status;
	    }

	    public Long getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(Long createdAt) {
	        this.createdAt = createdAt;
	    }

}
