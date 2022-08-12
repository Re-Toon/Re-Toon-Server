package retoon.retoon_server.src.webtoon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class GetWebtoonListGenreRes {
    private String webtoonImgUrl;
    private String name;
    private ArrayList<String> webtoonPlatform;
    private ArrayList<String> writerName;
    private float appStarRate;
    private ArrayList<String> webtoonGenre;
    private int ageLimit;
    private int likeNum;
}
