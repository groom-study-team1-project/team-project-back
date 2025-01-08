package deepdivers.community.domain.member.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

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
@DynamicUpdate
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

    @Embedded
    private ProfileImage image;

    @Column(length = 100, nullable = false)
    private String aboutMe;

    @Embedded
    private Nickname nickname;

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

    @Column(nullable = false, length = 50)
    private String job;

    private Member(final MemberSignUpRequest request, final Encryptor encryptor) {
        this.email = new Email(request.email());
        this.password = new Password(encryptor, request.password());
        this.nickname = new Nickname(request.nickname());
        this.phoneNumber = new PhoneNumber(request.phoneNumber());
        this.activityStats = ActivityStats.createDefault();
        this.image = ProfileImage.createDefault();
        this.aboutMe = StringUtils.EMPTY;
        this.githubAddr = StringUtils.EMPTY;
        this.blogAddr = StringUtils.EMPTY;
        this.role = MemberRole.NORMAL;
        this.status = MemberStatus.REGISTERED;
        this.job = StringUtils.EMPTY;
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
        Optional.ofNullable(request.nickname()).ifPresent(nickname -> this.nickname = this.nickname.update(nickname));
        this.phoneNumber.update(request.phoneNumber());
        Optional.ofNullable(request.aboutMe()).ifPresent(about -> this.aboutMe = about);
        Optional.ofNullable(request.githubUrl()).ifPresent(github -> this.githubAddr = github);
        Optional.ofNullable(request.blogUrl()).ifPresent(blog -> this.blogAddr = blog);
        Optional.ofNullable(request.job()).ifPresent(j -> this.job = j);
    }

    public void validateStatus() {
        switch (this.status) {
            case DORMANCY -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_DORMANCY);
            case UNREGISTERED -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_UNREGISTER);
        }
    }

    public void resetPassword(final Encryptor encryptor, final String password) {
        // todo test
        this.password.reset(encryptor, password);
    }

    public void changePassword(final Encryptor encryptor, final UpdatePasswordRequest request) {
        // todo test
        if (!encryptor.matches(request.currentPassword(), this.getPassword())) {
            throw new BadRequestException(MemberExceptionType.INVALID_PASSWORD);
        }
        if (encryptor.matches(request.newPassword(), this.getPassword())) {
            throw new BadRequestException(MemberExceptionType.ALREADY_USING_PASSWORD);
        }
        resetPassword(encryptor, request.newPassword());
    }

    public void incrementCommentCount() {
        activityStats.incrementCommentCount();
    }

    public void incrementPostCount() {
        activityStats.incrementPostCount();
    }

    public void updateProfileImage(final String imageKey, final String imageUrl) {
        image = ProfileImage.of(imageKey, imageUrl);
    }

}
