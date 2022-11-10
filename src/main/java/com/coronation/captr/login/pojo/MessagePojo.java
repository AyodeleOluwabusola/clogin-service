package com.coronation.captr.login.pojo;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @author toyewole
 */
@Getter
@Setter
public class MessagePojo implements Serializable {

    private static final long serialVersionUID = 5492676866498222484L;
    private String subject;
    private String attachment;
    private String messageType;
    private String recipient;
    private String source;
    private String messageBody;
    private String requestTime;
    private String code;
}
