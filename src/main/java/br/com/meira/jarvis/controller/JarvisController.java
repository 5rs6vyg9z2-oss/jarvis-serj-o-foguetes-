package br.com.meira.jarvis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JarvisController {
    @GetMapping("/status")
    public String status() {
        return "Jarvis API ta on.";
    }
}
