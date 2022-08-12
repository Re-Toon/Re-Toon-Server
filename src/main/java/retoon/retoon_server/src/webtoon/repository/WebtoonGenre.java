package retoon.retoon_server.src.webtoon.repository;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
class WebtoonGenreKey implements Serializable {
    //WebtoonGenre의 다중키를 만들기 위해 다중키 속성이 들어있는 별도의 클래스 생성
    private String genreName;
    private int webtoonIdx;
}

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Entity
@Table(name = "WebtoonGenre")
public class WebtoonGenre {
    @EmbeddedId //PK가 genreName과 webtoonIdx 두개의 합성으로 이루어진 다중키를 만들기 위해 EmbeddedId 어노테이션 사용
    @Column(name = "WebtoonGenre")
    private WebtoonGenreKey webtoonGenre;

    //Webtoon과 Genre는 서로 여러개 할당 받을 수 있는 ManyToMany 관계이므로 설정해줌
    @ManyToMany(mappedBy = "genres")
    private List<Webtoon> webtoonList = new ArrayList<Webtoon>();
}