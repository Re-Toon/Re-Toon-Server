package retoon.retoon_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Spring Security 의존성을 추가했으나 아직 인증단계를 개발하지 않은 상태로 exclude를 이용해 적용되지 않도록 설정
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ReToonServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReToonServerApplication.class, args);
    }

}
