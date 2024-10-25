package com.udipoc.api.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum Status {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private static final Map<String, Status> statusByValue = new HashMap<String, Status>();

    static {
        for (Status type : Status.values()) {
            statusByValue.put(type.status, type);
        }
    }

    final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status getStatus(String value) {
        return statusByValue.get(value);
    }
}

