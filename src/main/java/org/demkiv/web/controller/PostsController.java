package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.impl.PostsServiceImpl;
import org.demkiv.web.model.form.PostForm;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PostsController {
    private final PostsServiceImpl postsService;

    @PostMapping(value = "/api/post",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePost(
            @RequestParam("post") String post,
            @RequestParam("person_id") long personId) {
        PostForm postForm = PostForm.builder()
                .post(post)
                .personId(personId)
                .build();

        postsService.saveEntity(postForm);
        return ResponseModel.builder()
                .mode("Success")
                .build();
    }

    @GetMapping(value = "/api/person/{id}/posts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> getAllPostsForPerson(@PathVariable String id) {
        List<?> posts = postsService.findEntity(id);
        return ResponseModel.builder()
                .mode("Success")
                .body(posts)
                .build();
    }
}
