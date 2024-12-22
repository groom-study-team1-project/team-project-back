package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.docs.MemberApiControllerDocs;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberQueryRepository;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.global.security.jwt.Auth;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;
    private final PostQueryRepository postQueryRepository;
    private final MemberQueryRepository memberQueryRepository;

    @GetMapping("/me/{memberId}")
    public ResponseEntity<API<MemberProfileResponse>> me(
            @Auth final Member member,
            @PathVariable final Long memberId
    ) {
        final MemberProfileResponse memberProfile = memberQueryRepository.getMemberProfile(memberId, member.getId());
        return ResponseEntity.ok(API.of(MemberStatusType.GET_PROFILE_SUCCESS, memberProfile));
    }

    @PutMapping("/me")
    public ResponseEntity<NoContent> updateProfile(
        @Auth final Member member,
        @Valid @RequestBody final MemberProfileRequest request
    ) {
        final NoContent response = memberService.updateProfile(member, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<NoContent> updatePassword(
        @Auth final Member member,
        @RequestBody @Valid final UpdatePasswordRequest request
    ) {
        final NoContent response = memberService.changePassword(member, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<API<List<AllMyPostsResponse>>> allWrittenPosts(
        @Auth final Member member,
        @RequestParam(required = false) final Long categoryId,
        @RequestParam(required = false) final Long lastPostId
    ) {
        return ResponseEntity.ok(API.of(
                MemberStatusType.GET_MY_POSTS_SUCCESS,
                postQueryRepository.findAllMyPosts(member.getId(), lastPostId, categoryId)
        ));
    }

}
