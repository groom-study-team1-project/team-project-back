package deepdivers.community.domain.file.repository.jpa;

import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.file.repository.entity.File;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFileRepository extends JpaRepository<File, Long> {

    List<File> findAllByReferenceIdAndFileType(Long referenceId, FileType fileType);

    void deleteAllByReferenceIdAndFileType(Long referenceId, FileType fileType);

}
