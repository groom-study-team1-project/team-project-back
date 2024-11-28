package deepdivers.community.domain.hashtag.service;

import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.post.model.Post;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public Set<PostHashtag> updatePostHashtags(final Post post, final List<String> newHashtags) {
        final List<PostHashtag> currentHashtags = postHashtagRepository.findAllByPostId(post.getId());
        postHashtagRepository.deleteAll(currentHashtags);

        return createPostHashtags(post, newHashtags);
    }

    public Set<PostHashtag> createPostHashtags(final Post post, final List<String> newHashTags) {
        return newHashTags.stream()
                .map(this::getOrCreateHashtag)
                .map(hashtag -> PostHashtag.of(post, hashtag))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Hashtag getOrCreateHashtag(final String hashtag) {
        return hashtagRepository.findByHashtag(hashtag)
            .orElseGet(() -> hashtagRepository.save(Hashtag.from(hashtag)));
    }

}
