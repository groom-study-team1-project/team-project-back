package deepdivers.community.domain.file.repository;

import static deepdivers.community.domain.file.repository.entity.QFile.file;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.file.application.interfaces.FileQueryRepository;
import deepdivers.community.domain.file.repository.entity.FileType;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileQueryRepositoryImpl implements FileQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAllImageUrlsByPost(final Long postId) {
        return queryFactory
            .select(file.fileUrl)
            .from(file)
            .where(file.referenceId.eq(postId), file.fileType.eq(FileType.POST_CONTENT))
            .fetch();
    }

    public Map<Long, List<String>> findAllSlideImageUrlByPosts(final List<Long> postIds) {
        return queryFactory.select(post.id, file.fileUrl)
            .from(post)
            .leftJoin(file).on(file.referenceId.eq(post.id))
            .where(post.id.in(postIds), file.fileType.eq(FileType.POST_SLIDE))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(file.fileUrl)));
    }

}
