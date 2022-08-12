package retoon.retoon_server.src.webtoon.repository;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Webtoon")
public class Webtoon {
    @Id
    @Column(name = "WebtoonIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long WebtoonIdx;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(length = 2000, nullable = false)
    private String introduce;

    @Column(columnDefinition = "FLOAT", nullable = false)
    private float appStarRate;

    @Column(columnDefinition = "INT", nullable = false)
    private int ageLimit;

    @Column(columnDefinition = "INT", nullable = false)
    private int likeNum;

    @ManyToMany
    //다대다 관계를 일대다 - 다대일 관계로 연결해줄 테이블
    @JoinTable(name="WEBTOON_GENRE_CONN",
                joinColumns = @JoinColumn(name = "WebtoonIdx"),     //Webtoon과 연결시켜줄 연결테이블의 컬럼명
                inverseJoinColumns = @JoinColumn(name = "WebtoonGenre"))    //Genre와 연결시켜줄 연결테이블의 컬럼 = "
    private List<WebtoonGenre> webtoonGenres = new ArrayList<WebtoonGenre>();

    @ManyToMany
    @JoinTable(name = "WEBTOON_PLATFORM_CONN",
                joinColumns = @JoinColumn(name = "WebtoonIdx"),
                inverseJoinColumns = @JoinColumn(name = "WebtoonPlatform"))
    private List<WebtoonPlatform> webtoonPlatforms = new ArrayList<WebtoonPlatform>();

    @ManyToMany
    @JoinTable(name = "WEBTOON_WRITER_CONN",
                joinColumns = @JoinColumn(name = "WebtoonIdx"),
                inverseJoinColumns = @JoinColumn(name = "WebtoonWriter"))
    private List<WebtoonWriter> webtoonWriters = new ArrayList<WebtoonWriter>();




}
