package deepdivers.community.domain.post.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponse {

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
