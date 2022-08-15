package retoon.retoon_server.src.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import retoon.retoon_server.src.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 이메일로 사용자 조회(object)
    User findByEmail(String email);

    // 이메일로 사용자 조회(boolean)
    boolean existsByEmail(String email);

    // userIdx로 사용자 조회(object)
    User findByUserIdx(int userIdx);

    // userIdx로 사용자 조회(boolean)
    boolean existsByUserIdx(int userIdx);

}
