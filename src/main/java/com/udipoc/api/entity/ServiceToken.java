package com.udipoc.api.entity;

import com.udipoc.api.util.enums.ServiceName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceToken implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    private Long id;

    @Enumerated(EnumType.STRING)
    private ServiceName serviceName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date generateTimestamp;

    private String token;
}
