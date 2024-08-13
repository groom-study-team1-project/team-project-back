package deepdivers.commuity.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Contact {

    @Column(nullable = false, length = 20)
    private String tel;

    @Column(length = 100)
    private String githubAddr;

    @Column(length = 200)
    private String blogAddr;

}
