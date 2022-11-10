package com.coronation.captr.login.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author toyewole
 */
@Setter
@Getter
public class ChangePasswordReq {

    private String token ;
    private String newPassword;
    private String confirmPassword;
    private String email;

}
