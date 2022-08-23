package retoon.retoon_server.src.login.converter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import retoon.retoon_server.src.login.entity.Provider;
import retoon.retoon_server.src.user.social.SocialLoginType;

@Configuration
public class ProviderConverter implements Converter<String, Provider>{
    @Override
    public Provider convert(String s){
        return Provider.valueOf(s.toUpperCase());
    }
}