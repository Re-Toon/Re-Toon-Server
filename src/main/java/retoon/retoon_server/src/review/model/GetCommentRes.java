package retoon.retoon_server.src.review.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import retoon.retoon_server.src.review.entity.Comment;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class GetCommentRes {
    private String nickname;
    private String commentText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAT;

        /* Entity -> Dto*/

    public GetCommentRes(Comment comment) {

        this.nickname = comment.getUser().getNickname();
        this.commentText = comment.getCommentText();
        this.createdAT = comment.getCreatedAT();
    }

}