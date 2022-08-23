package retoon.retoon_server.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, 200, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    EMPTY_REVIEW_TEXT(false, 2004, "리뷰 내용을 입력해주세요."),
    INVALID_REVIEW_STAR_RATE(false, 2005, "올바른 평점 값이 아닙니다."),

    INVALID_REVIEW_IDX(false, 2006, "존재하지 않는 리뷰입니다."),
    INVALID_REVIEW_USER(false, 2007, "리뷰를 작성한 유저가 아닙니다."),
    INVALID_REVIEW_LIKE(false, 2008, "해당 리뷰의 좋아요가 존재하지 않습니다."),
    INVALID_REVIEW_LIKE_USER(false, 2009, "리뷰 좋아요를 누른 유저가 아닙니다."),
    EMPTY_REVIEW_LIKE(false, 2010, "리뷰 좋아요가 존재하지 않습니다."),
    EMPTY_REVIEW_UNLIKE(false, 2011, "리뷰 싫어요가 존재하지 않습니다."),

    FAILED_ON_SERVER(false, 100, "예기치 못한 에러가 발생했습니다."),

    // /users
    NOT_EXIST_USERS(false, 2100, "존재하지 않는 사용자입니다."),
    EMPTY_USER_NICKNAME(false, 2101, "닉네임을 입력해주세요."),
    INSUFFICIENT_USER_GENRE_LIST(false, 2102, "선호하는 장르 4가지를 선택해주세요."),
    NOT_LOGIN_USER(false, 2103, "로그인 되지 않은 사용자입니다."),

    // [POST] /users
    EMPTY_USER_NAME(false, 2104, "사용자 이름을 입력해주세요."),

    // [POST] /users /email
    EMPTY_USER_EMAIL(false, 2105, "이메일을 입력해주세요."),
    POST_USERS_EXISTS_EMAIL(false, 2106, "중복된 이메일이 존재합니다."),
    POST_USERS_INVALID_EMAIL(false, 2107, "이메일 형식을 확인해주세요."),

    // [POST] /users /password
    EMPTY_USER_PASSWORD(false, 2108, "비밀번호를 입력해주세요."),
    EMPTY_USER_CHECK_PASSWORD(false, 2109, "비밀번호를 한번 더 입력해주세요."),
    NOT_EQUAL_PASSWORD(false, 2110, "비밀번호가 일치하지 않습니다."),
    POST_USERS_INVALID_PASSWORD(false, 2111, "비밀번호는 적어도 1개 이상의 영문, 숫자, 특수문자를 사용하여 8~16자를 입력해주세요."),
    PASSWORD_ENCRYPTION_ERROR(false, 2112, "비밀번호 암호화에 실패했습니다."),

    // [POST] /users /login
    FAILED_TO_LOGIN(false, 2113, "로그인에 실패했습니다."),

    // [POST] /users /follow
    NOT_EXISTS_FOLLOW_INFO(false, 2114, "팔로우 정보가 존재하지 않습니다."),
    EXISTS_FOLLOW_INFO(false, 2116, "팔로우 정보가 이미 존재합니다."),

    // [POST] /users /profile
    POST_USERS_EXISTS_NICKNAME(false, 2115, "중복된 닉네임이 존재합니다."),

    /**
     * 3000 : Response 오류
     */
    //Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    NOT_VALID_REFRESH_TOKEN(false, 3100, "유효하지 않은 토큰입니다."),
    NOT_VALID_AUTH_TOKEN(false, 3101, "일치하는 이메일 인증 토큰을 찾을 수 없습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 오류가 발생했습니다.")
    ;


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}
