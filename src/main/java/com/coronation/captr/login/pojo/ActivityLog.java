package com.coronation.captr.login.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author toyewole
 */
@Data
public class ActivityLog implements Serializable {
    private String activityType;
    private String description;
    private String emailAddress;
    private String requestTime;
}
