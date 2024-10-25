package com.udipoc.api.util.enums;

public enum ErrorCode {
    IMAGE_EXTRACTION_EXCEPTION("0002", "Exception throws while image extraction");


    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
    }
