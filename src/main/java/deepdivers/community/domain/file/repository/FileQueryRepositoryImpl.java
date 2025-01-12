package deepdivers.community.domain.file.repository;

import static deepdivers.community.domain.file.repository.entity.QFile.file;

import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.file.application.interfaces.FileQueryRepository;
import deepdivers.community.domain.file.repository.entity.FileType;
import java.util.List;
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

}
