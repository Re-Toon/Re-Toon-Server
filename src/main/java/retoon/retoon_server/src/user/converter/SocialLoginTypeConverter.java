package retoon.retoon_server.src.user.converter;

/**
 * Controller 에서 Social Login Type parameter
 * enum type 대문자 -> 소문자 mapping
 * localhost:8080/auth/Google -> localhost:8080/auth/Google
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import retoon.retoon_server.src.user.social.SocialLoginType;

@Configuration
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {
    @Override
    public SocialLoginType convert(String s){
        return SocialLoginType.valueOf(s.toUpperCase());
    }
}