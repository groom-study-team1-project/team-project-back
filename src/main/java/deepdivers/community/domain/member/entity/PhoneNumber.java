package deepdivers.community.domain.member.entity;

import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class PhoneNumber {

    private static final Pattern PATTERN =
        Pattern.compile("^01(?:0|1|[6-9])-(?!0000)\\d{4}-(?!0000)\\d{4}$");

    @Column(name = "phoneNumber", nullable = false, length = 20)
    private String value;

    private void validatePhoneNumber(final String phoneNumber) {
        if (!PATTERN.matcher(phoneNumber).matches()) {
            throw new BadRequestException(MemberExceptionCode.INVALID_PHONE_NUMBER_FORMAT);
        }
    }

    protected void update(final String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.value = phoneNumber;
    }

    protected PhoneNumber(final String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.value = phoneNumber;
    }

}
