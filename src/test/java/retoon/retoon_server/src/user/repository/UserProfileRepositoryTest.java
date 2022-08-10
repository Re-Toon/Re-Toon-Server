package retoon.retoon_server.src.user.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.Id;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserProfileRepositoryTest {
    @Autowired
    UserProfileRepository userProfileRepository;

    // 유저 프로필 생성 API Test - Test 성공
    // Test code 실행 시 반드시 @Test 삽입 유의
    @Test
    // Table 사이의 mapping 시에는 반드시 파스칼케이스 문법을 활용
    public void create(){
        // 새로운 유저 객체 생성
        UserProfile userProfile = new UserProfile();
        userProfile.setNickname("Shin");
        userProfile.setIntroduce("Hello");
        userProfile.setImgUrl("imgUrl2");

        // 반복적으로 객체 리스트를 삽입
        for(int i = 0; i < 4; i++){
            Genre genre = new Genre();
            genre.setGenreName("genre" + i);
            userProfile.addGenre(genre); // 유저의 장르 리스트에 genre 삽입
        }

        userProfileRepository.save(userProfile);
        // DB 반영
        userProfileRepository.flush();
    }

    // 유저 프로필 수정 API Test - Test 성공
    @Test
    public void update(){
        // 유저 인덱스 설정
        int userIdx = 1;
        // 유저 인덱스를 통한 객체 반환
        UserProfile userProfile = userProfileRepository.findByUserIdx(userIdx);
        userProfile.setNickname("Yummy"); // 닉네임 수정
        userProfile.setIntroduce("Bye"); // 자기소개 수정
        userProfile.setImgUrl("imgUrl3"); // 이미지 수정
        for(int i = 0; i < 4; i++){
            Genre genre = userProfile.getGenres().get(i);
            genre.setGenreName(i + "genre"); // 장르 수정
        }
        userProfileRepository.save(userProfile);
        userProfileRepository.flush(); // DB 반영
    }

    @Test
    public void delete(){
        int userIdx = 2;
        // 유저 인덱스를 통한 객체 반환
        UserProfile userProfile = userProfileRepository.findByUserIdx(userIdx);
        userProfile.setStatus("INACTIVE"); // 상태를 비활성화로 변경
        userProfileRepository.save(userProfile);
        userProfileRepository.flush(); // DB 반영
    }

}

