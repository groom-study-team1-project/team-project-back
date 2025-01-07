package deepdivers.community.domain.image.application.interfaces;

import java.util.List;

public interface ImageQueryRepository {

    List<String> findAllImagesByPost(Long postId);

}
