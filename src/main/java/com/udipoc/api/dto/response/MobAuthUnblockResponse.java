package com.udipoc.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class MobAuthUnblockResponse implements Serializable {

    private static final long serialVersionUID = 7156526077883281623L;

    private String message;
}
