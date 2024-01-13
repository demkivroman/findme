package org.demkiv.web.controller;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.service.impl.PersonFinderImpl;
import org.demkiv.web.model.PersonModel;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final PersonFinderImpl finder;

    @PostMapping(value = "/api/search",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePost(@RequestParam("item") String item) {
        if (item.isEmpty()) {
            throw new RuntimeException("Empty search query.");
        }
        List<PersonModel> foundPersons = finder.findEntity(item);
        return ResponseModel.builder()
                .mode("Success")
                .body(foundPersons)
                .build();
    }
}
