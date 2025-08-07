package com.smart.q.smartq.dto;

import java.util.Set;

public class UserInfoResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;

    // Constructors
    public UserInfoResponse() {}

    public UserInfoResponse(String userId, String email, String firstName, String lastName, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}