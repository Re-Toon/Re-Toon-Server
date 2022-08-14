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

    FAILED_ON_SERVER(false, 100, "예기치 못한 에러가 발생했습니다.")
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
