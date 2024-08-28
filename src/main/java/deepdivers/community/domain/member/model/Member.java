package deepdivers.community.domain.member.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import deepdivers.community.utility.encryptor.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_nickname",
                columnNames = {"nickname"}
        )
)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private Password password;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 100, nullable = false)
    private String aboutMe;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Contact contact;

    @Embedded
    private ActivityStats activityStats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Builder
    public Member(final MemberSignUpRequest request, final Encryptor encryptor) {
        this.email = request.email();
        this.password = Password.of(encryptor, request.password());
        this.role = MemberRole.NORMAL;
        this.nickname = Nickname.from(request.nickname());
        this.imageUrl = request.imageUrl();
        this.aboutMe = StringUtils.EMPTY;
        this.contact = Contact.from(request.phoneNumber());
        this.activityStats = ActivityStats.createDefault();
        this.status = MemberStatus.REGISTERED;
    }

    public static Member of(final MemberSignUpRequest request, final Encryptor encryptor) {
        return new Member(request, encryptor);
    }

    public String getPassword() {
        return this.password.getValue();
    }

    public String getNickname() {
        return nickname.getValue();
    }

}
