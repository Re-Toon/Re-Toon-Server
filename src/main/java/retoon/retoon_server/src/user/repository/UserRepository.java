package retoon.retoon_server.src.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import retoon.retoon_server.src.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 이메일로 사용자 조회
    User findByEmail(String email);

    // userIdx로 사용자 조회
    User findByUserIdx(int userIdx);
}
