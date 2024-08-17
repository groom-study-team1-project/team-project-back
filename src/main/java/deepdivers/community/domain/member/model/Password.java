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
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Password {

    private static final Pattern PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$");

    @Column(name = "password", nullable = false, length = 100)
    private String val;

    public static Password of(final Encryptor encryptor, final String password) {
        if (Objects.isNull(password) || !PATTERN.matcher(password).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
        return new Password(encryptor.encrypt(password));
    }

}
