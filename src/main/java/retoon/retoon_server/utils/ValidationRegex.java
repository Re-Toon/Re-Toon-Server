package retoon.retoon_server.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    // 이메일 정규 표현식
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        // compile : 주어진 정규표현식에서 패턴을 구성
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        // matcher : 대상 문자열이 패턴과 일치할 경우 true 반환
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 비밀번호는 8~16자 영문, 숫자, 특수문자를 사용
    public static boolean isRegexPassword(String target){
        // ^ : start to string, (?=.*[]) : [] at least once, {} : string length
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

}