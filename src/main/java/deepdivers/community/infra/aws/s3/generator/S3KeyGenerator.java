package deepdivers.community.infra.aws.s3.generator;

import deepdivers.community.infra.aws.s3.KeyType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3KeyGenerator {

    public static String generateProfileKey(final String contentType, final String uuid) {
        return String.format("%s%s/%s", KeyType.PROFILE.getPrefix(), uuid, generateFileName(contentType));
    }

    public static String generatePostKey(final String contentType, final String uuid) {
        return String.format("%s%s/%s", KeyType.POST.getPrefix(), uuid, generateFileName(contentType));
    }

    private static String generateFileName(final String contentType) {
        return getFileBaseName()
               + "_"
               + System.currentTimeMillis()
               + "."
               + contentType.substring(contentType.lastIndexOf("/") + 1);
    }

    private static String getFileBaseName() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}
