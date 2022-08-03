package retoon.retoon_server.login.social;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class NaverOauthTest {

    @Test
    public void convert(){
        String unicode_name = "\\uc2e0\\uc720\\ubbf8";
        //유니코드를 본래 언어로 변환 및 이름 정보 반환
        String str = unicode_name.replace("\\",""); //'\'을 ''로 변환
        String[] arr = str.split("u"); //'u'를 기준으로 문자를 분리
        //이름을 반환할 변수를 설정
        StringBuilder name = new StringBuilder();

        for(int i = 1; i < arr.length; i++){
            int hexVal = Integer.parseInt(arr[i], 16); //Array ["", "c2e0", "c720", "bbf8"] arr[0]은 공백이므로 1로 시작
            name.append((char) hexVal);
        }

        System.out.print(name);
    }

}
