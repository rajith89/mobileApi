package com.udipoc.api.entity;

import com.udipoc.api.util.enums.Status;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Slf4j
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    protected void onCreate() {
        String currentPrincipalName = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            currentPrincipalName = authentication.getName();
        } catch (Exception e) {
            log.info("User get issue on spring context");
        }
        createdDate = LocalDateTime.now(ZoneId.systemDefault());
        createdBy = currentPrincipalName;
    }

    @PreUpdate
    protected void onUpdate() {
        String currentPrincipalName = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            currentPrincipalName = authentication.getName();
        } catch (Exception e) {
            log.info("User get issue on spring context");
        }
        updatedDate = LocalDateTime.now(ZoneId.systemDefault());
        updatedBy = currentPrincipalName;
    }
}
