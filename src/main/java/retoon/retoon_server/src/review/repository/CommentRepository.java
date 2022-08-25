package retoon.retoon_server.src.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.review.entity.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
