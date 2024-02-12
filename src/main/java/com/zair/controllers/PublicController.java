package com.zair.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public")
public class PublicController {

    @GetMapping
    public String publicGetEndpoint() {
        return "This message is returned because you accessed a public GET endpoint.";
    }

    @PostMapping
    public String publicPostEndpoint() {
        return "This message is returned because you accessed a public POST endpoint.";
    }

    @PutMapping
    public String publicPutEndpoint() {
        return "This message is returned because you accessed a public PUT endpoint.";
    }

    @DeleteMapping
    public String publicDeleteEndpoint() {
        return "This message is returned because you accessed a public DELETE endpoint.";
    }
}
