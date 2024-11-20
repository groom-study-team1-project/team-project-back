package deepdivers.community.domain.hashtag.service;

import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import jakarta.transaction.Transactional;
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

    public Set<PostHashtag> connectPostWithHashtag(final Post post, final List<String> newHashtagNames) {
        Set<String> currentHashtagNames = getCurrentHashtagNamesForPost(post.getId());

        removeUnusedHashtags(post.getId(), currentHashtagNames, newHashtagNames);

        Set<String> hashtagsToAdd = getHashtagsToAdd(currentHashtagNames, newHashtagNames);

        return createPostHashtags(post, hashtagsToAdd);
    }

    private Set<String> getCurrentHashtagNamesForPost(Long postId) {
        return postHashtagRepository.findByPostId(postId).stream()
                .map(postHashtag -> postHashtag.getHashtag().getName())
                .collect(Collectors.toSet());
    }


    private void removeUnusedHashtags(Long postId, Set<String> currentHashtagNames, List<String> newHashtagNames) {
        Set<String> toRemove = currentHashtagNames.stream()
                .filter(name -> !newHashtagNames.contains(name))
                .collect(Collectors.toSet());

        if (!toRemove.isEmpty()) {
            Set<Long> hashtagIds = hashtagRepository.findHashtagIdsByNames(toRemove);
            postHashtagRepository.deleteByPostIdAndHashtagIds(postId, hashtagIds);
        }
    }

    private Set<String> getHashtagsToAdd(Set<String> currentHashtagNames, List<String> newHashtagNames) {
        return newHashtagNames.stream()
                .filter(name -> !currentHashtagNames.contains(name))
                .collect(Collectors.toSet());
    }

    private Set<PostHashtag> createPostHashtags(Post post, Set<String> hashtagsToAdd) {
        return hashtagsToAdd.stream()
                .peek(this::validateHashtag)
                .map(this::getOrCreateHashtag)
                .map(hashtag -> PostHashtag.of(post, hashtag))
                .collect(Collectors.toSet());
    }

    private Hashtag getOrCreateHashtag(final String hashtag) {
        return hashtagRepository.findByHashtag(hashtag)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(hashtag)));
    }

    private void validateHashtag(String hashtag) {
        if (!hashtag.matches("^[\\p{L}\\p{N}]{1,10}$")) {
            throw new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT);
        }
    }

}
