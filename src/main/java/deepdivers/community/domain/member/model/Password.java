package deepdivers.community.domain.member.model;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.utility.encryptor.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Password {

    private static final Pattern PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$");

    @Column(name = "password", nullable = false, length = 100)
    @Getter
    private String value;

    public static Password of(final Encryptor encryptor, final String password) {
        final String passwordAfterTrimmed = password.trim();
        if (!PATTERN.matcher(passwordAfterTrimmed).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
        return new Password(encryptor.encrypt(passwordAfterTrimmed));
    }

    public void matches(final Encryptor encryptor,final String password) {
        final Boolean isCorrect = encryptor.matches(password, this.value);
        if (!isCorrect) {
            throw new BadRequestException(MemberExceptionType.INVALID_MEMBER_PASSWORD);
        }
    }

}
