package com.udipoc.api.entity;

import com.udipoc.api.util.enums.Status;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_session", schema = "mobile")
public class UserSession extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "login_status")
    @Enumerated(EnumType.STRING)
    private Status loginStatus;
}
