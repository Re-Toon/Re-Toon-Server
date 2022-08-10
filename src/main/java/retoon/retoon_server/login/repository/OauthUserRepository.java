package retoon.retoon_server.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthUserRepository extends JpaRepository<OauthUser, Integer> {

    //이메일로 사용자 조회
    OauthUser findByEmail(String email);
}
