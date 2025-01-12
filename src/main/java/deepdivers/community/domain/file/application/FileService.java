package deepdivers.community.domain.file.application;

import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.file.repository.jpa.JpaFileRepository;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import deepdivers.community.infra.aws.s3.S3TagManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final JpaFileRepository imageRepository;
    private final S3PresignManager s3PresignManager;
    private final S3TagManager s3TagManager;

    public void createPostContentImage(final List<String> imageKeys, final Long postId) {
        final List<File> postFiles = imageKeys.stream()
            .map(imageKey -> {
                s3TagManager.removeDeleteTag(imageKey);
                final String imageUrl = s3PresignManager.generateAccessUrl(imageKey);
                return File.createPostContentImage(imageKey, imageUrl, postId);
            })
            .toList();

        imageRepository.saveAll(postFiles);
    }

    public void updatePostContentImage(final List<String> newImageKeys, final Long postId) {
        imageRepository.findAllByReferenceIdAndFileType(postId, FileType.POST_CONTENT)
            .forEach(image -> s3TagManager.markAsDeleted(image.getFileKey()));

        imageRepository.deleteAllByReferenceIdAndFileType(postId, FileType.POST_CONTENT);
        createPostContentImage(newImageKeys, postId);
    }

}
