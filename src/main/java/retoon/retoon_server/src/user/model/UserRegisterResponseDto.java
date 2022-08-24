package retoon.retoon_server.src.user.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterResponseDto {
    private int userIdx;
    private String email;
    private String authToken;

    @Builder
    public UserRegisterResponseDto(int userIdx, String email, String authToken) {
        this.userIdx = userIdx;
        this.email = email;
        this.authToken = authToken;
    }
}
