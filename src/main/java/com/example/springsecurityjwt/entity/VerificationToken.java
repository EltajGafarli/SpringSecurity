package com.example.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Transient
    private final int EXPIRATIONTIME = 10;

    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_VERIFICATION"))
    private User user;

    public VerificationToken(User user, String token){
        this.token = token;
        this.user = user;
        this.expirationTime = this.calculateTime(EXPIRATIONTIME);
    }

    public VerificationToken(String token){
        this.token = token;
        this.expirationTime = calculateTime(EXPIRATIONTIME);
    }

    private Date calculateTime(int time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, time);
        return new Date(calendar.getTime().getTime());
    }

    public Date getExpirationTime(){
        return this.expirationTime;
    }
}
