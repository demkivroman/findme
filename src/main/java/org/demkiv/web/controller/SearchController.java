package org.demkiv.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.service.impl.PersonFinderImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {
    private final PersonFinderImpl finder;

    @PostMapping(value = "/api/search",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<?> savePost(@RequestParam("item") String item) {
        if (item.isEmpty()) {
            log.error("Empty search query.");
            throw new FindMeServiceException("Empty search query.");
        }
        return finder.findEntity(item);
    }
}
