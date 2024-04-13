package com.ecommerce;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Hidden
public class DemoController {

    @GetMapping("/status")
    public String getStatus(){
        return "The server is up and running";
    }

    @GetMapping("/adminInfo")
    public String getAdminInfo(){
        return "<h1>ADMIN ACCOUNT</h1></br><h2>Email: </h2><p>votruonglam2109@gmail.com</p></br><h2>Password: </h2><p>21092003@</p>";
    }
}
