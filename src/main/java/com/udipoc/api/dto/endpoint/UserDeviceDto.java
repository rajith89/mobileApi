package com.udipoc.api.dto.endpoint;

import com.udipoc.api.entity.UserDevice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDeviceDto {
    private boolean isBlocked;
    private boolean isDeviceExists;
    private UserDevice userDevice;
    private boolean isTimeDurationValid;
    private boolean isLoggedIn;
    private LocalDateTime attemptTime;
}
