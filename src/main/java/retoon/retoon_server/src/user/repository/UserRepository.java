package retoon.retoon_server.src.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 찾기

    Optional<User> findByUserIdx(int userIdx); // userIdx로 사용자 찾기

    boolean existsByEmail(String email); // 이메일로 사용자 조회(boolean)

    boolean existsByUserIdx(int userIdx); // userIdx로 사용자 조회(boolean)

    boolean existsByNickname(String nickname); // 닉네임으로 사용자 조회(boolean)

    Optional<User> findByEmailAndProvider(String email, String provider); // 이메일과 로그인 타입으로 회원 찾기
}
