package deepdivers.community.domain;


import deepdivers.community.domain.image.domain.ImageType;
import deepdivers.community.domain.image.repository.entity.Image;
import deepdivers.community.domain.image.repository.jpa.JpaImageRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import deepdivers.community.infra.aws.s3.properties.S3Properties;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.Tag;


@SpringBootTest
@Import(LocalStackTestConfig.class)
@DirtiesContext
@Transactional
public class IntegrationTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired EntityManager entityManager;
    @Autowired JpaImageRepository imageRepository;

    @Autowired protected S3Client s3Client;
    @Autowired protected S3Properties s3Properties;
    @Autowired protected S3PresignManager s3PresignManager;

    protected Member getMember(Long id) {
        return memberRepository.findById(id).get();
    }

    protected Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    protected List<Image> getPostContentImages(Long id) {
        return imageRepository.findAll()
            .stream()
            .filter(image -> image.getReferenceId().equals(id) && image.getImageType() == ImageType.POST_CONTENT)
            .toList();
    }

    protected Comment getComment(Long id) {
        return commentRepository.findById(id).get();
    }

    protected void cacheClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected void createTestObject(String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString("test content"));
    }

    protected String generateAccessUrl(String key) {
        return s3PresignManager.generateAccessUrl(key);
    }

    protected List<Tag> getTag(String key) {
        GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        GetObjectTaggingResponse response = s3Client.getObjectTagging(getTaggingRequest);
        return response.tagSet();
    }

}
