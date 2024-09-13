package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.post.controller.docs.CommentControllerDocs;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CommentController implements CommentControllerDocs {

    @GetMapping
    public void example() {

    }

}
