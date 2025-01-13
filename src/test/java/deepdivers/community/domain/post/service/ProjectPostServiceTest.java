package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.common.PostRequestFactory;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProjectPostServiceTest extends IntegrationTest {

    @Autowired private ProjectPostService projectPostService;

    @BeforeEach
    void setUp() {
        createTestObject("default-image/posts/thumbnail.png");
        createTestObject("posts/image2.png");
        createTestObject("posts/image3.png");
        createTestObject("posts/image4.png");
        createTestObject("posts/image5.png");
    }

}