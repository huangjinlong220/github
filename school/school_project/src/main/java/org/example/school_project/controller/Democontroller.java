package org.example.school_project.controller;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
@RequestMapping("/demo/")
public class Democontroller {
    @GetMapping("/he/")
    public String he(){
        return "hello world";
    }
}
