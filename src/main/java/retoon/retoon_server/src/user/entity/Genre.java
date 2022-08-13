package retoon.retoon_server.src.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // 빈 기본 생성자 생략 가능
@Entity
// @EqualsAndHashCode(callSuper = false, exclude = {"userProfile"}) // JPA, lombok 함께 사용할 경우, hashcode 중복 생성으로 java.lang.StackOverflowError 발생, 해결을 위한 중복제거
public class Genre {

  //primary key
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성 규칙
  private int genreIdx;

  @Column(length = 45, nullable = false)
  private String genreName;

  // 유저 프로필과의 관계 설정 / 양방향
  // @JsonIgnore // Java에 Json 타입 변환 과정에서 발생하는 참조 순환 오류에 대비
  @ManyToOne
  @JoinColumn(name = "userIdx")
  @JsonBackReference // 순환참조 문제를 해결하기 위해 적용, 직렬화가 되지 않도록 수행
  private UserProfile userProfile;

  // @Builder
  // public Genre(String genreName) {
  //    this.genreName = genreName;
  // }


  // lombok, JPA를 같이 사용하는 경우에는 순환참조 오류 발생, 직접 무한 참조를 끊어도 되지만, getter, setter 해제 적용
  public String getGenreName() { return genreName; }
  public UserProfile getUserProfile() { return userProfile; }

  public void setGenreName(String genreName) { this.genreName = genreName; }
  public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

}
