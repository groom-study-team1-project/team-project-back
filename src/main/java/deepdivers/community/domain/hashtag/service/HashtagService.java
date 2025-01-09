package deepdivers.community.domain.hashtag.service;

import deepdivers.community.domain.hashtag.entity.Hashtag;
import deepdivers.community.domain.hashtag.entity.PostHashtag;
import deepdivers.community.domain.hashtag.repository.jpa.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.jpa.PostHashtagRepository;
import deepdivers.community.domain.post.entity.Post;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public void updatePostHashtags(final Post post, final List<String> newHashtags) {
        final List<PostHashtag> currentHashtags = postHashtagRepository.findAllByPostId(post.getId());
        postHashtagRepository.deleteAll(currentHashtags);
        createPostHashtags(post, newHashtags);
    }

    public void createPostHashtags(final Post post, final List<String> hashTags) {
        final LinkedHashSet<PostHashtag> postHashtags = hashTags.stream()
            .map(this::getOrCreateHashtag)
            .map(hashtag -> PostHashtag.of(post, hashtag))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        postHashtagRepository.saveAll(postHashtags);
    }

    private Hashtag getOrCreateHashtag(final String hashtag) {
        return hashtagRepository.findByHashtag(hashtag)
            .orElseGet(() -> hashtagRepository.save(Hashtag.from(hashtag)));
    }

}
