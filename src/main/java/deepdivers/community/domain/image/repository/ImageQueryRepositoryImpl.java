package deepdivers.community.domain.image.repository;

import static deepdivers.community.domain.image.repository.entity.QImage.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.image.application.interfaces.ImageQueryRepository;
import deepdivers.community.domain.image.domain.ImageType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageQueryRepositoryImpl implements ImageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAllImagesByPost(final Long postId) {
        return queryFactory
            .select(image.imageUrl)
            .from(image)
            .where(image.referenceId.eq(postId), image.imageType.eq(ImageType.POST_CONTENT))
            .fetch();
    }

}
