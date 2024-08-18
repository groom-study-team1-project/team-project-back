package deepdivers.community.domain.member.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.dto.request.info.MemberRegisterInfo;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;

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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    public Member(final String nickname, final String imageUrl, final String phoneNumber) {
        this.role = MemberRole.NORMAL;
        this.nickname = Nickname.from(nickname);
        this.imageUrl = imageUrl;
        this.aboutMe = StringUtils.EMPTY;
        this.contact = Contact.from(phoneNumber);
        this.activityStats = ActivityStats.createDefault();
        this.status = MemberStatus.REGISTERED;
    }

    public static Member registerMember(final MemberRegisterInfo memberRegisterInfo) {
        return new Member(
                memberRegisterInfo.nickname(),
                memberRegisterInfo.imageUrl(),
                memberRegisterInfo.phoneNumber()
        );
    }

    public String getNickname() {
        return nickname.getValue();
    }

}
