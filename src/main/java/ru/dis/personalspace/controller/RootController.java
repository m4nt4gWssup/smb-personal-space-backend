package ru.dis.personalspace.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class RootController {

    /**
     * Main method that redirects to Swagger UI.
     *
     * @return Redirect string to Swagger UI
     */
    @GetMapping(value = "/")
    public String root() {
        return "redirect:swagger-ui/index.html";
    }
}
