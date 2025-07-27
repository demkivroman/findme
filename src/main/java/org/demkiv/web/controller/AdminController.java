package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdminController {

    @GetMapping(value = "/api/ping",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String status() {
        return "Findme, pong";
    }
}
