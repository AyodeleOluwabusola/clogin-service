package com.coronation.captr.login.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author KeanHive
 * 
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public class BaseLongEntity extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = -4682309251557966107L;

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, insertable = true, updatable = false)
    private Long id;

    protected BaseLongEntity() { }

}

