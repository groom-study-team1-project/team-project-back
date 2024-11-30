package deepdivers.community.domain.post.service;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostImage;
import deepdivers.community.domain.post.repository.PostImageRepository;
import deepdivers.community.global.utility.uploader.S3Uploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static deepdivers.community.global.utility.uploader.S3Uploader.POST_DIRECTORY;
import static deepdivers.community.global.utility.uploader.S3Uploader.TEMP_DIRECTORY;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final PostImageRepository postImageRepository;
    private final S3Uploader s3Uploader;

    public String uploadImageToTemp(final MultipartFile imageFile) {
        return s3Uploader.postImageUpload(imageFile);
    }

    public List<PostImage> updatePostImages(final Post post, final List<String> newImageUrls) {
        final List<PostImage> currentImageUrls = postImageRepository.findAllByPostId(post.getId());
        postImageRepository.deleteAll(currentImageUrls);

        return createPostImages(post, newImageUrls);
    }

    public List<PostImage> createPostImages(final Post post, final List<String> newImageUrls) {
        return newImageUrls.stream()
                .filter(this::isValidImageUrl)
                .map(imageUrl -> moveAndGenereate(post, imageUrl))
                .toList();
    }

    private PostImage moveAndGenereate(final Post post, final String imageUrl) {
        if (imageUrl.contains(POST_DIRECTORY)) {
            return PostImage.of(post, imageUrl);
        }

        return PostImage.of(post, moveTempImageToPostBucket(imageUrl, post.getId()));
    }

    private boolean isValidImageUrl(final String imageUrl) {
        return imageUrl.contains(TEMP_DIRECTORY) || imageUrl.contains(POST_DIRECTORY);
    }

    private String moveTempImageToPostBucket(final String tempImageUrl, final Long postId) {
        final String fileName = tempImageUrl.split(TEMP_DIRECTORY)[1];
        final String tempKey = s3Uploader.buildTempKey(fileName);
        final String finalKey = s3Uploader.buildPostKey(postId, fileName);

        return s3Uploader.moveImage(tempKey, finalKey);
    }

}

