package com.coronation.captr.login.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author toyewole
 */

@Getter
@Setter
public class AuthResponse extends Response {
    private String firstName;
    private String lastName;
    private String token ;


}
