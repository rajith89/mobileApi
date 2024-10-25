package com.udipoc.api.repository;

import com.udipoc.api.entity.UserSession;
import com.udipoc.api.util.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {

    @Query("select us from UserSession us where us.username = :username and us.status = :status")
    Optional<UserSession> findByUsername(@Param("username") String username, @Param("status") Status status);

    @Query("select us from UserSession us where us.username = :username and us.status = :status")
    UserSession findUserSessionByUsernameAndStatus(@Param("username") String username, @Param("status") Status status);

    @Query("select us from UserSession us where us.username = :username and us.status = :status and us.loginStatus = :loginStatus and us.token = :token")
    Optional<UserSession> findUserIsLoginOrNot(@Param("username") String username, @Param("status") Status status, @Param("loginStatus") Status loginStatus, @Param("token") String token);

}
