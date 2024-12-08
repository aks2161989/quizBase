package com.example.quizBase.service;

import com.example.quizBase.dao.UserRepository;
import com.example.quizBase.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    public Optional<User> findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.existsByEmail(email);
    }

    public List<User> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        return users != null ? users : Collections.emptyList();
    }
    public void createUser(User user){
        userRepository.save(user);
    }
    public User authenticate(String username, String password) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        username = username.toLowerCase();

        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    public void registerUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new IllegalArgumentException("This username already exists. Please choose another");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("This Email is already in use");
        }
        if(user.getFull_name() == null){
            throw new IllegalArgumentException("Full name cannot be blank");
        }
        this.createUser(user);

    }


    }
