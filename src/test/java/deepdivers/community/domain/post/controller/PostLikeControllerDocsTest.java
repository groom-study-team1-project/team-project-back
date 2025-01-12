package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.like.dto.code.LikeStatusCode;
import deepdivers.community.domain.like.service.LikeService;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.resolver.AuthorizationResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import deepdivers.community.domain.ControllerTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostLikeControllerDocsTest extends ControllerTest {

    @MockBean
    private LikeService likeService;


}