package com.coronation.captr.login.enums;

public enum MessageType {

    EMAIL, SMS;

    public static MessageType getEnumType(String enumName) {
        for (MessageType docTypeEnum : MessageType.values()) {
            if (docTypeEnum.name().equals(enumName)) {
                return docTypeEnum;
            }
        }
        return null;
    }
}
