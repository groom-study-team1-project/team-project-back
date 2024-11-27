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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final PostImageRepository postImageRepository;
    private final S3Uploader s3Uploader;

    public String uploadImageToTemp(final MultipartFile imageFile){
        return s3Uploader.postImageUpload(imageFile);
    }

    public List<PostImage> connectPostWithImage(final Post post, final List<String> newImageUrls) {
        final List<String> currentImageUrls = getCurrentImageUrlsForPost(post.getId());

        removeUnusedImages(post.getId(), currentImageUrls, newImageUrls);

        return createPostImages(post, newImageUrls);
    }

    private List<String> getCurrentImageUrlsForPost(final Long postId) {
        return postImageRepository.findByPostId(postId).stream()
                .map(PostImage::getImageUrl)
                .toList();
    }

    private void removeUnusedImages(final Long postId, final List<String> currentImageUrls, final List<String> newImageUrls) {
        final List<String> toRemove = currentImageUrls.stream()
                .filter(url -> url.contains(String.format("/%s/", S3Uploader.POST_DIRECTORY)) && !newImageUrls.contains(url))
                .toList();

        if (!toRemove.isEmpty()) {
            toRemove.forEach(imageUrl -> postImageRepository.deleteByPostIdAndImageUrl(postId, imageUrl));
        }
    }

    private List<PostImage> createPostImages(final Post post, final List<String> newImageUrls) {
        return newImageUrls.stream()
                .filter(url -> url.contains(String.format("/%s/", S3Uploader.TEMP_DIRECTORY)))
                .map(tempImageUrl -> {
                    String movedImageUrl = moveTempImageToPostBucket(tempImageUrl, post.getId());
                    return new PostImage(post, movedImageUrl);
                })
                .toList();
    }

    private String moveTempImageToPostBucket(final String tempImageUrl, final Long postId) {
        final String fileName = tempImageUrl.split(String.format("/%s/", S3Uploader.TEMP_DIRECTORY))[1];

        final String tempKey = s3Uploader.buildTempKey(fileName);
        final String finalKey = s3Uploader.buildPostKey(postId, fileName);

        return s3Uploader.moveImage(tempKey, finalKey);
    }

}

