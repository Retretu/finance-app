package org.gouenji.financeapp.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicAuthorizationController {

    @GetMapping("/login")
    public String getLoginPage() {
        return "public/authorization/login-page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage() {
        return "public/authorization/registration-page";
    }
}
