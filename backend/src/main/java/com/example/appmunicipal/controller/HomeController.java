package com.example.appmunicipal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/bienvenida")
    public String home() {
        return "Bienvenido a la App Municipal!";
    }
}
