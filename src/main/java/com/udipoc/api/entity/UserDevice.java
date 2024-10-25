package com.udipoc.api.entity;

import com.udipoc.api.util.enums.BlockType;
import com.udipoc.api.util.enums.ServiceName;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_devices", schema = "mobile")
public class UserDevice implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uin")
    private String uin;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "attempt_time")
    private LocalDateTime attemptTime;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "is_blocked")
    @Enumerated(EnumType.STRING)
    private BlockType isBlocked;

    @Column(name = "is_logged_in")
    private boolean isLoggedIn;
}
