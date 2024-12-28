package com.game.social.discovery.authentication.Repository;

import com.game.social.discovery.authentication.Model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, BigInteger> {
    Optional<UserAccount> findByEmailId(String emailId);
}
