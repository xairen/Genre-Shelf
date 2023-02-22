package com.example.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

@Controller
public class GreetingController {

    @GetMapping("/home.html")
    public String home(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        return "home";
    }

    @GetMapping("/booklist.html")
    public String booklist(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        return "booklist";
    }

    @GetMapping("/genre.html")
    public String genre(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        return "genre";
    }

    @GetMapping("/greeting.html")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name",name);
        return "greeting";
    }

    @RequestMapping("/")
    @ResponseBody
    public String goToHomePage () throws IOException {
        File trainData = new File("src/main/resources/data/train"); //location of file for train data
        genre gn = new genre(trainData);
        File testData = new File("src/main/resources/data/test");	//location of file for test data
        System.out.println("Total number of classes being classified = "+gn.getNumClasses());
        gn.classifyGenre(testData);
        return "<h1>This is the Home page</h1>";
    }
}