package com.coronation.captr.login.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author toyewole
 */

@Getter
@Setter
@Entity
@Table(name = "CT_USER")
public class User extends BaseEntity {

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstname;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PASSSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "CONFIRMATION_TIMESTAMP")
    private LocalDateTime emailConfirmationTimestamp;

    @Column(name = "CONFIRMATION_CODE")
    private String confirmationCode;

}
