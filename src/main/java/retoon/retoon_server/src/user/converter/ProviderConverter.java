package retoon.retoon_server.src.user.converter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import retoon.retoon_server.src.user.entity.Provider;

@Configuration
public class ProviderConverter implements Converter<String, Provider>{
    @Override
    public Provider convert(String s){
        return Provider.valueOf(s.toUpperCase());
    }
}