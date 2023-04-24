package com.example.springsecurityjwt.event;


import com.example.springsecurityjwt.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.bind.annotation.GetMapping;

@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private String applictionUrl;

    public RegistrationCompleteEvent(User user, String applictionUrl) {
        super(user);
        this.user = user;
        this.applictionUrl = applictionUrl;
    }
}
