package com.example.springsecurityjwt.service;

import com.example.springsecurityjwt.entity.PasswordToken;
import com.example.springsecurityjwt.entity.User;
import com.example.springsecurityjwt.entity.VerificationToken;
import com.example.springsecurityjwt.model.UserModel;
import com.example.springsecurityjwt.repository.PasswordTokenRepository;
import com.example.springsecurityjwt.repository.UserRepository;
import com.example.springsecurityjwt.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordTokenRepository passwordTokenRepository;

    @Override
    @Transactional
    public User registerUser(UserModel userModel) {
        User user = User.builder()
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .email(userModel.getEmail())
                .role("USER")
                .enabled(false).build();

        this.userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user){
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateUser(String token) {

        VerificationToken byToken = verificationTokenRepository.findByToken(token);

        if(byToken == null){
            return "Bad Request";
        }

        Date expirationTime = byToken.getExpirationTime();
        Calendar calendar = Calendar.getInstance();

        if(expirationTime.getTime() - calendar.getTime().getTime() <= 0){
            verificationTokenRepository.delete(byToken);
            return "expired";
        }

        User user = byToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "Verified Succesfully";
    }

    public VerificationToken generateNewVerificationToken(String oldToken){
        VerificationToken byToken = verificationTokenRepository.findByToken(oldToken);
        byToken.setToken(UUID.randomUUID().toString());
        VerificationToken saved = verificationTokenRepository.save(byToken);
        return saved;

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordToken passwordToken = new PasswordToken(user, token);
        passwordTokenRepository.save(passwordToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {

        PasswordToken byToken = passwordTokenRepository.findByToken(token);

        if(byToken == null){
            return "Invalid";
        }

        Calendar cal = Calendar.getInstance();
        if(byToken.getExpirationTime().getTime() - cal.getTime().getTime() <= 0){
            passwordTokenRepository.delete(byToken);
            return "Expired";
        }


        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        PasswordToken byToken = passwordTokenRepository.findByToken(token);
        return Optional.ofNullable(byToken.getUser());
    }

    @Override
    public void chageUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkOldPassword(User user, String oldPassword) {

        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        return matches;
    }
}
