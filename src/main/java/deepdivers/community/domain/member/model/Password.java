package deepdivers.community.domain.member.model;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private static final Pattern PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$");

    @Column(name = "password", nullable = false, length = 100)
    @Getter
    private String value;

    private void validatePassword(final String password) {
        if (!PATTERN.matcher(password).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
    }

    protected Password(final Encryptor encryptor, final String password) {
        validatePassword(password);
        this.value = encryptor.encrypt(password);
    }

    protected void reset(final Encryptor encryptor, final String password) {
        validatePassword(password);
        this.value = encryptor.encrypt(password);
    }

}
