package retoon.retoon_server.src.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.review.entity.Review;
import retoon.retoon_server.src.review.entity.ReviewLike;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewIdx(Long reviewIdx);
    @Query(value = "select r from Review r where r.webtoonIdx = :webtoonIdx")
    List<Review> findByWebtoonIdx(int webtoonIdx);

}
