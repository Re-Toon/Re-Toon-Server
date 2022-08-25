package retoon.retoon_server.src.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.user.entity.Follow;
import retoon.retoon_server.src.user.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    Follow findFollowByFromUserAndToUser(User fromUser, User toUser);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.fromUser.userIdx=:fromUserIdx")
    int findFollowingCountByToUserIdx(@Param("fromUserIdx") int userIdx); // 팔로잉 수, fromUserIdx 개수 측정

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.toUser.userIdx=:toUserIdx")
    int findFollowerCountByFromUserIdx(@Param("toUserIdx") int userIdx); // 팔로워 수, toUserIdx 개수 측정
}
