package com.coronation.captr.login.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author toyewole
 */

@Getter
@Setter
@Entity
@Table(name = "CT_USER")
public class User extends BaseEntity {

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PASSSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "CONFIRMATION_TIMESTAMP")
    private LocalDateTime emailConfirmationTimestamp;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "CT_USER_PRIVILEGE",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "privilege_id")})
    private Set<CTPrivilege> privilegeList = new HashSet<>();

}
