package deepdivers.community.domain.member.entity;

import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"lower"})
public class Nickname {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z가-힣][a-zA-Z0-9가-힣]*$");
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 20;

    @Column(name = "nickname", nullable = false, length = 20)
    private String value;

    @Column(name = "lower_nickname", nullable = false, length = 20)
    private String lower;

    public void validateNickname(final String nickname) {
        validateNicknameLength(nickname);
        validateNickNameFormat(nickname);
    }

    private void validateNicknameLength(final String nickname) {
        if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            throw new BadRequestException(MemberExceptionCode.INVALID_NICKNAME_LENGTH);
        }
    }

    private void validateNickNameFormat(final String nickname) {
        if (!PATTERN.matcher(nickname).matches()) {
            throw new BadRequestException(MemberExceptionCode.INVALID_NICKNAME_FORMAT);
        }
    }

    protected Nickname(final String nickname) {
        validateNickname(nickname);
        this.value = nickname;
        this.lower = nickname.toLowerCase(Locale.ENGLISH);
    }

    public Nickname update(final String nickname) {
        validateNickname(nickname);
        this.value = nickname;
        this.lower = nickname.toLowerCase(Locale.ENGLISH);

        return this;
    }

}
