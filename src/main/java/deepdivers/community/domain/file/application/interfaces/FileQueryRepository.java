package deepdivers.community.domain.file.application.interfaces;

import java.util.List;

public interface FileQueryRepository {

    List<String> findAllImageUrlsByPost(Long postId);

}
