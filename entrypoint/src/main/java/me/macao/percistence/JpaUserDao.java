package me.macao.percistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserDao
    extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
