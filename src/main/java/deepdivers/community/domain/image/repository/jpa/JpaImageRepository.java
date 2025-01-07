package deepdivers.community.domain.image.repository.jpa;

import deepdivers.community.domain.image.domain.ImageType;
import deepdivers.community.domain.image.repository.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByReferenceIdAndImageType(Long referenceId, ImageType imageType);

    void deleteAllByReferenceIdAndImageType(Long referenceId, ImageType imageType);

}
