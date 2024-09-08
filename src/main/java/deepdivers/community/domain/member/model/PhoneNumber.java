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
import lombok.Setter;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class PhoneNumber {

    private static final Pattern PATTERN =
        Pattern.compile("^01(?:0|1|[6-9])-(?!0000)\\d{4}-(?!0000)\\d{4}$");

    @Column(name = "phoneNumber", nullable = false, length = 20)
    private String value;

    public static void validator(final String phoneNumber) {
        if (!PATTERN.matcher(phoneNumber).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_PHONE_NUMBER_FORMAT);
        }
    }

    protected void update(final String phoneNumber) {
        this.value = phoneNumber;
    }

}
