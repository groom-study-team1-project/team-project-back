package deepdivers.community.domain.member.model;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class Email {

    private static final String REGEX =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Column(name = "email", nullable = false, length = 100)
    private String value;

    public static void validator(final String email) {
        if (!PATTERN.matcher(email).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_EMAIL_FORMAT);
        }
    }

}
