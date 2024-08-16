package deepdivers.community.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

    @Column(nullable = false, length = 20)
    private String tel;

    @Column(length = 100)
    private String githubAddr;

    @Column(length = 200)
    private String blogAddr;

    @Builder
    public Contact(final String tel) {
        this.tel = tel;
    }

    public static Contact createDefault(final String tel) {
        return new Contact(tel);
    }

}
