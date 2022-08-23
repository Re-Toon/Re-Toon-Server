package retoon.retoon_server.src.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.login.entity.EmailAuth;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    @Query("SELECT u FROM EmailAuth u WHERE u.email=:email and u.authToken=:authToken and u.expireDate >= :currentTime and u.expired=false")
    Optional<EmailAuth> findValidAuthByEmail(@Param("email") String email, @Param("authToken") String authToken, @Param("currentTime") LocalDateTime currentTime);
}
