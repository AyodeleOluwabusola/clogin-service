package com.coronation.captr.login.entities;

import com.coronation.captr.login.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "MESSAGE_TRAIL")
public class MessageTrail extends BaseLongEntity {

    @Column(name = "RECIPIENT")
    private String recipient;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "CODE")
    private String code;

    @Column(name = "REQUEST_TIME")
    private LocalDateTime requestTime;

    @Column(name = "MESSAGE_TYPE")
    @Enumerated(EnumType.STRING)
    private MessageType type;

}
