package retoon.retoon_server.src.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.review.entity.Review;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewIdx(Long reviewIdx);
}
