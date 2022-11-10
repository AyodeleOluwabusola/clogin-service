package com.coronation.captr.login.respositories;

import com.coronation.captr.login.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author toyewole
 */
public interface IUserRespository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email );

    @Modifying
    @Query("update User set password = :password, lastModified = current_timestamp where email =:email ")
    void updatePasswordAndLastModified (String password, String email);


}
