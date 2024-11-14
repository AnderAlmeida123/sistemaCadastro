package com.projeto.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalController {
    @GetMapping("/administrativo")
    public String administrativo() {
        return "administrativo/home";
    }
}
