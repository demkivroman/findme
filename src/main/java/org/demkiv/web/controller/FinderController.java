package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntityFinder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class FinderController {
    @Qualifier("finderService")
    private final EntityFinder<Long, Optional<?>> finderService;

    @GetMapping(value = "/api/finder/contact/info/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestorContactInfo(@PathVariable String id) {
        Optional<?> result = finderService.findEntity(Long.parseLong(id));
        return ResponseEntity.ok(result.orElseThrow(() -> new FindMeServiceException(
                String.format("Could not find person contact entity with id %s", id))));
    }
}
