package com.example.springsecurityjwt.event.listener;

import com.example.springsecurityjwt.entity.User;
import com.example.springsecurityjwt.event.RegistrationCompleteEvent;
import com.example.springsecurityjwt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener
        implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        userService.saveVerificationTokenForUser(token, user);

        String url = event.getApplictionUrl() + "/verify?token=" + token;
        log.info("Click the Link to Verify Your Account: {}", url);

    }
}
