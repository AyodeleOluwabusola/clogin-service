package com.coronation.captr.login.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author toyewole
 */
@Entity
@Data
@Table(name = "CT_PRIVILEGE")
public class CTPrivilege extends BaseEntity{

    @Column(name = "CODE", unique = true)
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NAME", unique = true)
    private String name;



}
