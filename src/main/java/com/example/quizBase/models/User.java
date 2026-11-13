package com.example.quizBase.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String full_name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_STUDENT;

    @OneToMany (mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzesCreated = new ArrayList<>();

    public User() {
    }

    public User(long id, String username, String password, String email, String full_name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.full_name = full_name;
        this.role = Role.ROLE_STUDENT;
    }

    public User(long id, String username, String password, String email, String full_name, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.full_name = full_name;
        this.role = role != null ? role : Role.ROLE_STUDENT;
    }

    public List<Quiz> getQuizzesCreated() {
        return quizzesCreated;
    }

    public void setQuizzesCreated(List<Quiz> quizzesCreated) {
        this.quizzesCreated = quizzesCreated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.ROLE_STUDENT;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", full_name='" + full_name + '\'' +
                ", role=" + role +
                '}';
    }
}
