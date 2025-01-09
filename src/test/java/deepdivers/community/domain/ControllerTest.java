package deepdivers.community.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.AuthHelper;
import deepdivers.community.global.security.AuthPayload;
import deepdivers.community.global.utility.encryptor.Encryptor;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@Import(Encryptor.class)
public class ControllerTest {

    @MockBean
    protected AuthHelper authHelper;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected Encryptor encryptor;

    @BeforeEach
    protected void setUp(WebApplicationContext webApplicationContext) throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    protected void mockingAuthArgumentResolver() {
        AuthPayload tokenPayload = new AuthPayload(1L, "1", "1", "", 1L, 1L);
        given(authHelper.resolveToken(any())).willReturn("token");
        given(authHelper.parseToken(anyString())).willReturn(tokenPayload);
        given(encryptor.matches(anyString(), anyString())).willReturn(true);

        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678");
        final Member member = Member.of(request, encryptor);

        ReflectionTestUtils.setField(member, "id", 1L);
        given(memberService.getMemberWithThrow(anyLong())).willReturn(member);
    }

}
