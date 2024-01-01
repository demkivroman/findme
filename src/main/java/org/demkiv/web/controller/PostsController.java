package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.PostsService;
import org.demkiv.web.model.PostForm;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @PostMapping(value = "/api/post",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePost(
            @RequestParam("post") String post,
            @RequestParam("person_id") long personId
    ) {
        PostForm postForm = PostForm.builder()
                .post(post)
                .personId(personId)
                .build();

        postsService.saveEntity(postForm);
        return ResponseModel.builder()
                .mode("Success")
                .build();
    }
}
