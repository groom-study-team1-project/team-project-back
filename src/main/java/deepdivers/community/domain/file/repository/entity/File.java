package deepdivers.community.domain.file.repository.entity;

import deepdivers.community.domain.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "deepdive_community_file")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class File extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String fileKey;

    @Column(nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(nullable = false)
    private Long referenceId;

    protected File(String fileKey, String fileUrl, FileType fileType, Long referenceId) {
        this.fileKey = fileKey;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.referenceId = referenceId;
    }

    public static File createPostContentImage(final String fileKey, final String fileUrl, final Long postId) {
        return new File(fileKey, fileUrl, FileType.POST_CONTENT, postId);
    }

}
