package retoon.retoon_server.src.webtoon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreReq;

import static retoon.retoon_server.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class WebtoonService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WebtoonDao webtoonDao;
    private final WebtoonProvider webtoonProvider;

    @Autowired
    public WebtoonService(WebtoonDao webtoonDao, WebtoonProvider webtoonProvider){
        this.webtoonDao = webtoonDao;
        this.webtoonProvider = webtoonProvider;
    }



}
