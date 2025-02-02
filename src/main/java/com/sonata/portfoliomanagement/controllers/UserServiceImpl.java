package com.sonata.portfoliomanagement.controllers;

import com.sonata.portfoliomanagement.interfaces.UserRepository;
import com.sonata.portfoliomanagement.model.User;
import com.sonata.portfoliomanagement.services.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private SecretKey secretKey;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Registration: Save the user with a hashed password
    @Override
    public void saveUser(User user) {
        try {
            // Encrypt the user's password before saving
            if(!user.isFirstLogin()){
                String encryptedPassword = AESUtil.decrypt(user.getPassword());
                user.setPassword(encryptedPassword);
                userRepository.save(user);
            }
            String encryptedPassword = AESUtil.encrypt(user.getPassword());
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Login: Retrieve the user and compare hashed passwords
    @Override
    public boolean userExistsByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        return user != null; // Return true if user exists, false otherwise
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public boolean verifyPassword(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // Use BCrypt's built-in method to verify the password
            String decryptedEncryptedPasswordFromDB = AESUtil.decrypt(user.getPassword());
            return Objects.equals(password, decryptedEncryptedPasswordFromDB);
        }
        return false;
    }


    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateFirstLogin(String email, String password, boolean isFirstLogin) {
        userRepository.updateFirstLoginByEmailAndPassword(email, password, isFirstLogin);
    }








}





