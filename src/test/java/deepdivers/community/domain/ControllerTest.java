package deepdivers.community.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import deepdivers.community.utility.encryptor.Encryptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

public class ControllerTest {

    @MockBean
    protected AuthHelper authHelper;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected Encryptor encryptor;

    protected void mockingAuthArgumentResolver() {
        AuthPayload tokenPayload = new AuthPayload(1L, "1", "1", 1L, 1L);
        given(authHelper.resolveToken(any())).willReturn("token");
        given(authHelper.parseToken(anyString())).willReturn(tokenPayload);
        given(encryptor.matches(anyString(), anyString())).willReturn(true);

        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678");
        final Member member = Member.of(request, encryptor);

        ReflectionTestUtils.setField(member, "id", 1L);
        given(memberService.getMemberWithThrow(anyLong())).willReturn(member);
    }

}
