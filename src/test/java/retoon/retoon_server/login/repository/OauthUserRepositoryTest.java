package retoon.retoon_server.login.repository;

import org.junit.After;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthUserRepositoryTest {
    @Autowired
    OauthUserRepository oauthUserRepository;

    @Test
    public void insert() {
        OauthUser oauthUser = new OauthUser();
        oauthUser.setName("shinyomi");
        oauthUser.setEmail("122017@inha.edu");
        oauthUser.setToken("123445454");

        oauthUserRepository.save(oauthUser);
        oauthUserRepository.flush();
    }

}
