//package com.example.calendit.service;
//
//import com.example.calendit.model.User;
//import com.example.calendit.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    @Transactional
//    public User saveOrUpdateUser(String email, String name, String picture, String googleId) {
//        Optional<User> existingUser = userRepository.findByEmail(email);
//
//        if (existingUser.isPresent()) {
//            User user = existingUser.get();
//            user.setName(name);
//            user.setPicture(picture);
//            user.setGoogleId(googleId);
//            return userRepository.save(user);
//        } else {
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setName(name);
//            newUser.setPicture(picture);
//            newUser.setGoogleId(googleId);
//            return userRepository.save(newUser);
//        }
//    }
//
//    public Optional<User> findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//}
package com.example.calendit.service;

import com.example.calendit.model.User;
import com.example.calendit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.example.calendit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<User> saveOrUpdateUser(String email, String name, String picture, String googleId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
        }
        user.setName(name);
        user.setPicture(picture);
        user.setGoogleId(googleId);
        user.setProvider("GOOGLE");

        return Optional.of(userRepository.save(user));
    }

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider("LOCAL");

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

