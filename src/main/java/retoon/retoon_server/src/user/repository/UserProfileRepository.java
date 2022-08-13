package retoon.retoon_server.src.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.user.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    //userIdx로 사용자 조회
    UserProfile findByUserIdx(int userIdx);
}
