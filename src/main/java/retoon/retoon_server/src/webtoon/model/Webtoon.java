package retoon.retoon_server.src.webtoon.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Webtoon {
    private int webtoonIdx;
    private String name;
    private String introduce;
    private float appStarRate;
    private int ageLimit;
    private String webtoonImgUrl;
    private ArrayList<String> writerName;
    private ArrayList<String> webtoonPlatform;
    private ArrayList<String> webtoonGenre;
    private int likeNum;
}

