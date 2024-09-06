package ru.egartech.staff.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("api")
    public String index() {
        return "index";
    }
}
