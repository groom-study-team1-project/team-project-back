package deepdivers.community.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    private static final String DEFAULT_IMAGE_KEY = "default-image/users/default-profile.png";
    private static final String DEFAULT_IMAGE_URL =
        "https://deepdiver-community-files-dev.s3.ap-northeast-2.amazonaws.com/" + DEFAULT_IMAGE_KEY;

    private String imageKey;

    @Column(nullable = false)
    private String imageUrl;

    public static ProfileImage of(String imageKey, String imageUrl) {
        return new ProfileImage(imageKey, imageUrl);
    }

    public static ProfileImage createDefault() {
        return new ProfileImage(DEFAULT_IMAGE_KEY, DEFAULT_IMAGE_URL);
    }

}
