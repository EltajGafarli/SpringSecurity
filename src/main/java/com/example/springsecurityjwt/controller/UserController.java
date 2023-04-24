package com.example.springsecurityjwt.controller;

import com.example.springsecurityjwt.entity.PasswordToken;
import com.example.springsecurityjwt.entity.User;
import com.example.springsecurityjwt.entity.VerificationToken;
import com.example.springsecurityjwt.event.RegistrationCompleteEvent;
import com.example.springsecurityjwt.model.PasswordModel;
import com.example.springsecurityjwt.model.UserModel;
import com.example.springsecurityjwt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(path = "/register")
    public User doRegister(@RequestBody UserModel userModel, final HttpServletRequest request){
        User user = this.userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, this.getApplicationUrl(request)));
        return user;
    }

    @PostMapping(path = "/verify")
    public String verifyUser(@RequestParam(name = "token") String token){
        String valid = userService.validateUser(token);
        return valid;
    }

    @GetMapping(path = "/resend")
    public String resendVerificationUrl(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, getApplicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping(path = "/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenEmail(user, getApplicationUrl(request), token);
        }

        return url;
    }

    @PostMapping(path = "/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){

        String valid = userService.validatePasswordResetToken(token);

        if(!"valid".equalsIgnoreCase(valid)){
            return "Invalid Token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isEmpty()) return "Invalid Token";

        userService.chageUserPassword(user.get(), passwordModel.getNewPassword());

        return "Password Reset Successfully";
    }

    @PostMapping(path = "/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User userByEmail = userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkOldPassword(userByEmail, passwordModel.getOldPassword())){
            return "Passsword is Not Found";
        }

        userService.chageUserPassword(userByEmail, passwordModel.getNewPassword());
        return "Password Changed Successfully";
    }

    private String passwordResetTokenEmail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl
                + "/savePassword?token="
                + token;
        log.info("Click this Url And change reset Password: {}", url);

        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url =
                applicationUrl
                        + "/verify?token="
                        + verificationToken.getToken();
        log.info(url);
    }

    private String getApplicationUrl(HttpServletRequest request){
        return "http://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                request.getContextPath();
    }

}
