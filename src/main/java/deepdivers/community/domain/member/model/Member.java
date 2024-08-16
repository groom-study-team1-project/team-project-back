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

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String imageUrl;

    @Embedded
    private Contact contact;

    @Embedded
    private ActivityStats activityStats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Builder
    public Member(
            final String nickname,
            final String imageUrl,
            final Contact contact,
            final ActivityStats activityStats
    ) {
        this.role = MemberRole.NORMAL;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.contact = contact;
        this.activityStats = activityStats;
        this.status = MemberStatus.REGISTERED;
    }

    public static Member registerMember(final MemberRegisterInfo memberRegisterInfo) {
        return new Member(
                memberRegisterInfo.nickname(),
                memberRegisterInfo.imageUrl(),
                Contact.createDefault(memberRegisterInfo.tel()),
                ActivityStats.createDefault()
        );
    }

}
