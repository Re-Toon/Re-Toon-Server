package retoon.retoon_server.src.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.review.entity.ReviewLike;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    @Query(value = "select r from ReviewLike r where r.review.reviewIdx = :reviewIdx and r.user.userIdx = :userIdx")
    Optional<ReviewLike> findByReviewIdxUserIdx(int userIdx, Long reviewIdx);
}