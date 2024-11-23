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
        List<String> currentImageUrls = getCurrentImageUrlsForPost(post.getId());

        removeUnusedImages(post.getId(), currentImageUrls, newImageUrls);

        List<String> imageUrlsToAdd = getImageUrlsToAdd(newImageUrls);

        return createPostImages(post, imageUrlsToAdd);
    }

    private List<String> getCurrentImageUrlsForPost(Long postId) {
        return postImageRepository.findByPostId(postId).stream()
                .map(PostImage::getImageUrl)
                .toList();
    }

    private void removeUnusedImages(Long postId, List<String> currentImageUrls, List<String> newImageUrls) {
        List<String> toRemove = currentImageUrls.stream()
                .filter(url -> url.contains("/posts/") && !newImageUrls.contains(url))
                .toList();

        if (!toRemove.isEmpty()) {
            toRemove.forEach(imageUrl -> postImageRepository.deleteByPostIdAndImageUrl(postId, imageUrl));
        }
    }

    private List<String> getImageUrlsToAdd(List<String> newImageUrls) {
        return newImageUrls.stream()
                .filter(url -> url.contains("/temp/"))
                .toList();
    }

    private List<PostImage> createPostImages(Post post, List<String> imageUrlsToAdd) {
        return imageUrlsToAdd.stream()
                .map(tempImageUrl -> {
                    String[] splitResults = tempImageUrl.split("/temp/");
                    String movedImageUrl = moveTempImageToPostBucket(splitResults[1], post.getId());
                    return new PostImage(post, movedImageUrl);
                })
                .toList();
    }

    private String moveTempImageToPostBucket(String fileName, Long postId) {
        String tempKey = String.format("temp/%s", fileName);
        String finalKey = String.format("posts/%d/%s", postId, fileName);

        return s3Uploader.moveImage(tempKey, finalKey);
    }
}

