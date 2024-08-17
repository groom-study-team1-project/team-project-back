package deepdivers.community.domain.member.model;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class Contact {
    private static final Pattern PATTERN = Pattern.compile("^01(?:0|1|[6-9])-(?!0000)\\d{4}-(?!0000)\\d{4}$");

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String githubAddr;

    @Column(length = 200)
    private String blogAddr;

    @Builder
    public Contact(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    private static void validatePhoneNumber(final String phoneNumber) {
        if (Objects.isNull(phoneNumber) || !PATTERN.matcher(phoneNumber).matches()) {
            throw new BadRequestException(MemberExceptionType.INVALID_PHONE_NUMBER_FORMAT);
        }
    }

    public static Contact fromPhoneNumber(final String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        return new Contact(phoneNumber);
    }

}
