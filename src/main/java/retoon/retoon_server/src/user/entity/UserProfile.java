package retoon.retoon_server.src.user.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
public class UserProfile {
    //primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성 규칙
    private int userIdx;

    @Column(length = 45, nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String introduce;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imgUrl;

    @Column(columnDefinition = "varchar(45) default 'ACTIVE'", nullable = false) // 유저 삭제를 위한 상태 column 추가
    private String status = "ACTIVE";

    // 유저 프로필마다 관심있는 장르 리스트가 존재
    @OneToMany(mappedBy = "userProfile",  targetEntity = Genre.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // 연관된 객체 정보도 함께 update, 변한 것만 추가
    @JsonManagedReference // 순환참조 문제를 해결하기 위해 수행, 직렬화를 수행
    private List<Genre> genres = new ArrayList<>();

    /*@Builder
    public UserProfile(String nickname, String introduce, String imgUrl) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.imgUrl = imgUrl;
        //this.getGenres().add(genre);
    }*/

    // 장르 추가 메소드
    public void addGenre(Genre genre){
        this.genres.add(genre);
        genre.setUserProfile(this); // 사용자 정보를 셋팅
    }

    public int getUserIdx() { return userIdx; }
    public String getNickname(){ return nickname; }
    public String getIntroduce() { return introduce; }
    public String getImgUrl() { return imgUrl; }
    public String getStatus() { return status; } // 유저 삭제를 위한 column 추가
    public List<Genre> getGenres() { return genres; }


    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    public void setStatus(String status) { this.status = status; } // 유저 삭제를 위한 상태 column 추가

    public void setGenres(List<Genre> genres) { this.genres = genres; }

}
