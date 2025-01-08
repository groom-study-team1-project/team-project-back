package deepdivers.community.domain.image.application;

import deepdivers.community.domain.image.repository.entity.ImageType;
import deepdivers.community.domain.image.repository.jpa.JpaImageRepository;
import deepdivers.community.domain.image.repository.entity.Image;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import deepdivers.community.infra.aws.s3.S3TagManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final JpaImageRepository imageRepository;
    private final S3PresignManager s3PresignManager;
    private final S3TagManager s3TagManager;

    public void createPostContentImage(final List<String> imageKeys, final Long postId) {
        final List<Image> postImages = imageKeys.stream()
            .map(imageKey -> {
                s3TagManager.removeDeleteTag(imageKey);
                final String imageUrl = s3PresignManager.generateAccessUrl(imageKey);
                return Image.createPostContentImage(imageKey, imageUrl, postId);
            })
            .toList();

        imageRepository.saveAll(postImages);
    }

    public void updatePostContentImage(final List<String> newImageKeys, final Long postId) {
        imageRepository.findAllByReferenceIdAndImageType(postId, ImageType.POST_CONTENT)
            .forEach(image -> s3TagManager.markAsDeleted(image.getImageKey()));

        imageRepository.deleteAllByReferenceIdAndImageType(postId, ImageType.POST_CONTENT);
        createPostContentImage(newImageKeys, postId);
    }

}
