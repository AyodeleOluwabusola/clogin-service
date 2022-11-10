package com.coronation.captr.login.respositories;


import com.coronation.captr.login.entities.MessageTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author toyewole
 */
public interface IMessageTrailsRespository extends JpaRepository<MessageTrail, Long> {

    Optional<MessageTrail> findTopByRecipientOrderByCreateDateDesc(String email);

}
