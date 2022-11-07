package com.coronation.captr.login.enums;


import com.coronation.captr.login.interfaces.IResponse;

/**
 * @author toyewole
 */
public enum IResponseEnum implements IResponse {
    ERROR(-1, "Error occurred while processing request"),
    EMAIL_EXIST(-2, "Email already exist"),
    SUCCESS(0, "Request processed successfully"),
    EMAIL_CONFIRMATION_ERROR(-3, "Email is yet to be confirmed!"),
    INVALID_VALIDATION_PARAM(-4,"Invalid Validation parameter. Kindly contact support. " ),
    INVALID_TOKEN(-5,"Invalid confirmation token" );

    int code;
    String desc;

    IResponseEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
