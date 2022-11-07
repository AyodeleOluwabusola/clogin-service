package com.coronation.captr.login.respositories;

import com.coronation.captr.login.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author toyewole
 */
public interface IUserRespository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email );


}
