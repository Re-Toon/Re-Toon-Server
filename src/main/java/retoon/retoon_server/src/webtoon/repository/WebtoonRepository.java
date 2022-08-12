package retoon.retoon_server.src.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebtoonRepository extends JpaRepository<WebtoonRepository, Long> {



}
