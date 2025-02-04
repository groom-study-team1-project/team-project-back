package deepdivers.community.domain.hashtag.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopularHashtagResponse {

    private Long hashtagId;
    private String hashtagName;

}
