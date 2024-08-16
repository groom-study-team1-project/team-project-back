package deepdivers.community.domain.member.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String role;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String imageUrl;

    @Embedded
    private Contact contact;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer postCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer commentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

}
