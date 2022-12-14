package com.federal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping("/home")
    public String getHomePage() {
        return "home.html";
    }

    @GetMapping("/query")
    public String getQueryPage() {
        return "query.html";
    }

    @GetMapping("/metro")
    public String getMetroPage() {
        return "metro.html";
    }

    // Tutorials
    @GetMapping("/react-tutorial")
    public String getReactTutorial() {
        return "react-tutorial.html";
    }

}
