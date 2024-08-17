package deepdivers.community.domain.member.model;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

    private static final Pattern PATTERN = Pattern.compile("^[0-9a-zA-Z가-힣]+(?:\\s+[0-9a-zA-Z가-힣]+)*$");
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 20;

    @Column(name = "nickname", nullable = false, length = 20)
    private String value;

    private static void validate(final String nickname) {
        validateNicknameLength(nickname);
        validateNickNameFormat(nickname);
    }

    private static void validateNicknameLength(final String nickname) {
        if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            throw new BadRequestException(MemberExceptionType.INVALID_NICKNAME_LENGTH);
        }
    }

    private static void validateNickNameFormat(final String nickname) {
        if (!PATTERN.matcher(nickname).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_NICKNAME_FORMAT);
        }
    }

    public static Nickname from(final String nickname) {
        validate(nickname);
        return new Nickname(nickname);
    }

}
