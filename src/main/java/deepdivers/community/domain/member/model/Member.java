package deepdivers.community.domain.member.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import deepdivers.community.global.exception.model.BadRequestException;
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
import java.util.Locale;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

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

    @Column(name = "lowerNickname", nullable = false, length = 20)
    private String lowerNickname;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(nullable = false, length = 100)
    private String githubAddr;

    @Column(nullable = false, length = 200)
    private String blogAddr;

    @Embedded
    private ActivityStats activityStats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private MemberStatus status;

    private Member(final MemberSignUpRequest request, final Encryptor encryptor) {
        this.email = new Email(request.email());
        this.password = new Password(encryptor, request.password());
        this.nickname = new Nickname(request.nickname());
        this.phoneNumber = new PhoneNumber(request.phoneNumber());
        this.lowerNickname = request.nickname().toLowerCase(Locale.ENGLISH);
        this.imageUrl = request.imageUrl();
        this.activityStats = ActivityStats.createDefault();
        this.aboutMe = StringUtils.EMPTY;
        this.githubAddr = StringUtils.EMPTY;
        this.blogAddr = StringUtils.EMPTY;
        this.role = MemberRole.NORMAL;
        this.status = MemberStatus.REGISTERED;
    }

    public static Member of(final MemberSignUpRequest request, final Encryptor encryptor) {
        return new Member(request, encryptor);
    }

    public String getPassword() {
        return this.password.getValue();
    }

    public String getNickname() {
        return this.nickname.getValue();
    }

    public String getPhoneNumber() {
        return this.phoneNumber.getValue();
    }

    public String getEmail() {
        return this.email.getValue();
    }

    public void updateProfile(final MemberProfileRequest request) {
        this.nickname.update(request.nickname());
        this.phoneNumber.update(request.phoneNumber());
        this.lowerNickname = request.nickname().toLowerCase(Locale.ENGLISH);
        updateProfileImage(request.imageUrl());
        updateAboutMe(request.aboutMe());
        updateGithub(request.githubUrl());
        updateBlog(request.blogUrl());
    }

    private void updateGithub(final String githubUrl) {
        if (!(githubUrl == null || githubUrl.isEmpty())) {
            this.githubAddr = githubUrl;
        }
    }

    private void updateBlog(final String blogUrl) {
        if (!(blogUrl == null || blogUrl.isEmpty())) {
            this.blogAddr = blogUrl;
        }
    }

    private void updateProfileImage(final String imageUrl) {
        if (!(imageUrl == null || imageUrl.isEmpty())) {
            this.imageUrl = imageUrl;
        }
    }

    private void updateAboutMe(final String aboutMe) {
        if (!(aboutMe == null || aboutMe.isEmpty())) {
            this.aboutMe = aboutMe;
        }
    }

    public void validateStatus() {
        switch (this.status) {
            case DORMANCY -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_DORMANCY);
            case UNREGISTERED -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_UNREGISTER);
        }
    }

}
