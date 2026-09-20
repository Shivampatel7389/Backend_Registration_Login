package com.reglog.repository;

import com.reglog.entity.JwtToken;
import com.reglog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    Optional<JwtToken> findByToken(String token);

    List<JwtToken> findByUser(User user);

    void deleteByExpiresAtBefore(LocalDateTime time);

    void deleteByUser(User user);
}
