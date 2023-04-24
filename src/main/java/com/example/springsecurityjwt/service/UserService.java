package com.example.springsecurityjwt.service;

import com.example.springsecurityjwt.entity.User;
import com.example.springsecurityjwt.entity.VerificationToken;
import com.example.springsecurityjwt.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);
    void saveVerificationTokenForUser(String token, User user);

    String validateUser(String token);
    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void chageUserPassword(User user, String newPassword);

    boolean checkOldPassword(User user, String oldPassword);
}
