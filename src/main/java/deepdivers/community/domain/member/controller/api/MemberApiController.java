package deepdivers.community.domain.member.controller.api;

import deepdivers.community.utility.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberApiController implements MemberApiControllerDocs {

    private final S3Uploader s3Uploader;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String member(@ModelAttribute MultipartFile file) {
        return s3Uploader.upload(file, 1L);
    }

}
