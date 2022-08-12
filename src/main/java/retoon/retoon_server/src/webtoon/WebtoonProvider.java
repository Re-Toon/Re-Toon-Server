package retoon.retoon_server.src.webtoon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreReq;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreRes;

import java.util.List;

import static retoon.retoon_server.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class WebtoonProvider {
    private final WebtoonDao webtoonDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WebtoonProvider(WebtoonDao webtoonDao){
        this.webtoonDao = webtoonDao;
    }


}
