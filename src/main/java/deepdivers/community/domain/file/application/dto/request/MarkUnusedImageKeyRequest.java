package deepdivers.community.domain.file.application.dto.request;

import java.util.List;

public record MarkUnusedImageKeyRequest(
    List<String> unusedImageKey
) {
}
