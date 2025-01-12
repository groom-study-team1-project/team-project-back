package deepdivers.community.domain;


import deepdivers.community.domain.hashtag.entity.Hashtag;
import deepdivers.community.domain.hashtag.entity.PostHashtag;
import deepdivers.community.domain.hashtag.repository.jpa.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.jpa.PostHashtagRepository;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.jpa.JpaFileRepository;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.member.repository.jpa.MemberRepository;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.comment.entity.Comment;
import deepdivers.community.domain.comment.repository.jpa.CommentRepository;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
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

    @Autowired EntityManager entityManager;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired PostHashtagRepository postHashtagRepository;
    @Autowired
    JpaFileRepository imageRepository;
    @Autowired HashtagRepository hashtagRepository;

    @Autowired protected S3Client s3Client;
    @Autowired protected S3Properties s3Properties;
    @Autowired protected S3PresignManager s3PresignManager;

    protected Member getMember(Long id) {
        return memberRepository.findById(id).get();
    }

    protected Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    protected List<File> getPostContentImages(Long id) {
        return imageRepository.findAll()
            .stream()
            .filter(image -> image.getReferenceId().equals(id) && image.getFileType() == FileType.POST_CONTENT)
            .toList();
    }

    protected List<PostHashtag> getHashtagsByPostId(Long postId) {
        return postHashtagRepository.findAllByPostId(postId);
    }

    protected Hashtag getHashtag(Long id) {
        return hashtagRepository.findById(id).get();
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
