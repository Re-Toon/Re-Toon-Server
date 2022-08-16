package retoon.retoon_server.src.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.review.entity.ReviewUnlike;

import java.util.Optional;

@Repository
public interface ReviewUnlikeRepository extends JpaRepository<ReviewUnlike, Long> {
    @Query(value = "select r from ReviewUnlike r where r.review.reviewIdx = :reviewIdx and r.user.userIdx = :userIdx")
    Optional<ReviewUnlike> findByReviewIdxUserIdx(int userIdx, Long reviewIdx);
}