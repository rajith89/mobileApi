package com.udipoc.api.repository;

import com.udipoc.api.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice,Long> {
    Optional<UserDevice> findByDeviceId(String deviceId);
    @Query("SELECT u FROM UserDevice u WHERE u.uin = ?1 AND isLoggedIn=?2")
    Optional<UserDevice> findByUinAndIsLoggedIn(String uin,boolean isLoggedIn);
}
