package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 사용자가 선택한 장르 리스트가 존재 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchGenreReq {
    private String genreName;
}
