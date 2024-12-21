package deepdivers.community.domain.member.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllMyPostsResponse {

    private Long id;
    private String title;
    private String thumbnail;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private Long memberId;
    private String memberNickname;
    private String memberJob;
    private boolean isLikedMe;

}
