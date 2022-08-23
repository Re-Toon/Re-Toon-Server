package retoon.retoon_server.src.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import retoon.retoon_server.src.login.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); // 이메일로 회원 찾기

    Optional<Member> findByEmailAndProvider(String email, String provider); // 이메일과 로그인 타입으로 회원 찾기
}
