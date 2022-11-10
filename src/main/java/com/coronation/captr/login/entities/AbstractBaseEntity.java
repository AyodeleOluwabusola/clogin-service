package com.coronation.captr.login.entities;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;


@MappedSuperclass
@Data
public abstract class AbstractBaseEntity<T> implements Serializable, Comparable<AbstractBaseEntity<T>> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseEntity.class);
    private static final long serialVersionUID = -7270322627438965338L;
    @Column(
            name = "ACTIVE",
            nullable = false,
            insertable = true,
            updatable = true
    )
    private boolean active = true;
    @Column(
            name = "DELETED",
            nullable = false,
            insertable = true,
            updatable = true
    )
    private boolean deleted;
    @Column(
            name = "CREATE_DATE",
            nullable = false,
            insertable = true,
            updatable = false
    )
    private LocalDateTime createDate;
    @Column(
            name = "LAST_MODIFIED",
            nullable = false,
            insertable = true,
            updatable = true
    )
    private LocalDateTime lastModified;

    public AbstractBaseEntity() {
    }

    public abstract T getId();

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getTableName()).append(" {").append(newLine);
        Field[] fields = this.getClass().getDeclaredFields();
        Field[] var4 = fields;
        int var5 = fields.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            if (!Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                result.append("  ").append(name).append(": ");

                try {
                    String prefix = "get";
                    if (field.getType().isAssignableFrom(Boolean.class)) {
                        prefix = "is";
                    }

                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    result.append(this.getClass().getMethod(prefix + name).invoke(this));
                } catch (InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException var10) {
                    logger.warn("Error reading the property {} from entity : {}", new Object[]{name, this.getClass().getName(), var10});
                }

                result.append(newLine);
            }
        }

        result.append('}');
        return result.toString();
    }

    public String getTableName() {
        Table table = (Table)this.getClass().getAnnotation(Table.class);
        if (table == null) {
            return "";
        } else {
            String tableName = table.name();
            return tableName;
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (this.getId() == null ? 0 : this.getId().hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            AbstractBaseEntity<T> other = (AbstractBaseEntity)obj;
            return this.getId() != null && other.getId() != null ? this.getId().equals(other.getId()) : false;
        }
    }

    public int compareTo(AbstractBaseEntity<T> other) {
        if (this.getId() == null) {
            return other.getId() == null ? 0 : -1;
        } else if (other.getId() == null) {
            return 1;
        } else if (!Comparable.class.isAssignableFrom(this.getId().getClass())) {
            throw new IllegalArgumentException("Class type of the pk does not implement the Comparable interface. " + this.getId().getClass().getName());
        } else {
            Comparable<T> thisComparablePk = (Comparable)this.getId();
            return thisComparablePk.compareTo(other.getId());
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.lastModified = this.createDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
