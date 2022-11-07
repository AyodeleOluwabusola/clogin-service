package com.coronation.captr.login.pojo;

import com.coronation.captr.login.interfaces.IResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author toyewole
 */
@Getter
@Setter
public class Response implements IResponse {
    int code;
    String description;
}
