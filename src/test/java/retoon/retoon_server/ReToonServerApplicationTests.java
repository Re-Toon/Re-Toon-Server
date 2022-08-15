package retoon.retoon_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class ReToonServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void regexCheck(){
        String password = "yumiyi54!!"; // 올바른 경우
        String password2 = "yy"; // 길이가 8~16이 아닌 경우
        String password3 = "yumiyumi!!"; // 숫자가 없는 경우
        String password4 = "yumiyumi45"; // 특수문자가 없는 경우
        String password5 = "124455!!"; // 영문자가 없는 경우
        String password6 = "Ydlsk35!"; // 대문자가 섞인 경우, 올바른 경우

        // ^ : start to string, (?=.*[]) : [] at least once, {} : string length
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(password);
        System.out.println(matcher.find());

        Matcher matcher1 = pattern.matcher(password2);
        System.out.println(matcher1.find());

        Matcher matcher2 = pattern.matcher(password3);
        System.out.println(matcher2.find());

        Matcher matcher3 = pattern.matcher(password4);
        System.out.println(matcher3.find());

        Matcher matcher4 = pattern.matcher(password5);
        System.out.println(matcher4.find());

        Matcher matcher5 = pattern.matcher(password6);
        System.out.println(matcher5.find());
    }
}
