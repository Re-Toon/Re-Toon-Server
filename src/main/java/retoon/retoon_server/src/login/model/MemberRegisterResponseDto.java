package retoon.retoon_server.src.login.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterResponseDto {
    private long id;
    private String email;
    private String authToken;

    @Builder
    public MemberRegisterResponseDto(Long id, String email, String authToken) {
        this.id = id;
        this.email = email;
        this.authToken = authToken;
    }
}
