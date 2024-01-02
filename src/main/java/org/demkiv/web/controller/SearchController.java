package org.demkiv.web.controller;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.service.PersonFinder;
import org.demkiv.web.model.PersonFoundForm;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final PersonFinder finder;

    @PostMapping(value = "/api/search",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePost(@RequestParam("item") String item) {
        List<PersonFoundForm> foundPersons = finder.findEntity(item);
        return ResponseModel.builder()
                .mode("Success")
                .body(foundPersons)
                .build();
    }
}
