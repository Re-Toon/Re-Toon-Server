package retoon.retoon_server.src.webtoon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreReq;
import retoon.retoon_server.src.webtoon.model.GetWebtoonListGenreRes;
import retoon.retoon_server.src.webtoon.model.Webtoon;

import java.util.List;


@RestController
@RequestMapping("/webtoon")
public class WebtoonController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final WebtoonProvider webtoonProvider;
    @Autowired
    private final WebtoonService webtoonService;
    
    public WebtoonController(WebtoonProvider webtoonProvider, WebtoonService webtoonService){
        this.webtoonProvider = webtoonProvider;
        this.webtoonService = webtoonService;
    }

}
