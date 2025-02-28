package com.dalbertang.live_chat_backend.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("users")
public class User {
    @Id
    private String id;
    private String email;
    private String name;
    private Set<String> roles;

    public User(String id, String email, String name, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public User() {}

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Set<String> getRoles() { return roles; }

    public void setRoles(Set<String> roles) { this.roles = roles; }
}
