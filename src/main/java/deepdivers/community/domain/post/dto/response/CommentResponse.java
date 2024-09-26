package deepdivers.community.domain.post.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private Long memberId;
    private String memberNickname;
    private String memberImageUrl;
    private boolean isModified;
    private boolean isLikedMe;

}
