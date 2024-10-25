package com.udipoc.api.util.enums;

public enum ResponseCode {
    SUCCESS_AUTH(2000, "Authentication Success."),
    SUCCESS_OTP_REQUEST(2001, "OTP send Success."),
    SUCCESS_AUTH_HISTORY(2002, "Fetch Auth History Success."),
    SUCCESS_LOGOUT_REQUEST(2003, "Logout Success."),
    SUCCESS_USER_UNBLOCK(2004, "User unblock success."),
    SUCCESS_ALREADY_LOGOUT(2005, "User already Logout."),

    FAIL_USER_DISABLED(1000, "User not active."),
    FAIL_AUTH(1001, "Authentication Failed."),
    FAIL_OTP_REQUEST(1002, "Please wait a couple of minutes and try again."),
    FAIL_UIN_NOT_FOUND(1003, "UIN not found."),
    FAIL_OTP_INVALIDATE(1004, "OTP invalidate. Generate OTP first."),
    FAIL_OTP_GENERATE(1004, "Failed to send otp "),

    RESIDENT_AUTH_LOCK_FAILED(1005, "Failed to lock auth modes"),
    RESIDENT_AUTH_UNLOCK_FAILED(1006, "Failed to unlock auth modes"),
    RESIDENT_AUTH_LOCK_SUCCES(1007, "Successfully locked authentication mode"),
    RESIDENT_AUTH_UNLOCK_SUCCES(1008, "Successfully unlocked authentication mode"),

    INVALID_REQUEST(1020, "Invalid Request"),
    INVALID_UIN(1021, "Invalid UIN"),
    FAILED_TO_CONNECT_MOSIP(1022, "Error connecting MOSIP"),
    INVALID_OTP(1023, "Invalid otp"),
    OTP_EXPIRED(1024, "OTP invalid or expired"),
    INVALID_OTP_OR_TRANSACTION_ID(1025, "Invalid otp or transaction id"),

    SUCCESS_RESIDENT_INFO(1030, "Resident info request successful."),
    FAIL_RESIDENT_INFO(1031, "Resident info request Failed."),

    RESOURCE_NOT_FOUND(3001, "Resource not found"),
    GENERAL_EXCEPTION(3002, "Please try again , something went wrong"),
    INTERNAL_SERVER_ERROR(3003, "Internal server error"),
    EMPTY_REQUEST_ERROR(3004, "Empty Request"),
    MOSIP_API_EXCEPTION(3005, "Mosip API Response Error."),
    MOSIP_API_ERROR_EXCEPTION(3006, "Mosip API Response Contains Errors"),
    MOSIP_API_401_EXCEPTION(3007, "Mosip API Response Error. 401"),
    RESIDENT_AUTH_HISTORY_ERROR(3008, "Failed to get authentication history"),
    RESIDENT_AUTH_HISTORY_MOSIP_ERROR(3009, "MOSIP API response error."),
    USER_DEVICE_BLOCKED(3010, "Device Blocked"),
    USER_DEVICE_NOT_BLOCKED(3011, "Device is not Blocked."),
    USER_DEVICE_UNBLOCK_FAILED(3012, "Device can not unblock"),
    MOBILENO_OR_TRANSACTIONID_NOT_FOUND(3013, "Mobile number or transaction ID Not Found"),
    USER_DEVICE_LOGGED_IN_ANOTHER_UIN(3014, "Device is logged in with another UIN"),
    USER_UIN_LOGGED_IN_ANOTHER_DEVICE(3015, "This UIN is already attached with a different device");

    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}
