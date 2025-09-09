package org.gouenji.financeapp.controller.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.service.UserService;
import org.gouenji.financeapp.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicAuthorizationController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public PublicAuthorizationController(UserService userService, JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error) {
        if(error != null) {
            model.addAttribute("isAuthenticationFailed", true);
        }
        return "public/authorization/login-page";
    }


    @GetMapping("/registration")
    public String getRegistrationPage() {
        return "public/authorization/registration-page";
    }


    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userService.loadUserByUsername(email);

            String token = jwtTokenUtil.generateToken(userDetails);

            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) jwtTokenUtil.getLifetime().getSeconds());
            response.addCookie(jwtCookie);

            return "redirect:/account";
        } catch (BadCredentialsException e) {
            return "redirect:/login?error";
        }

    }

    @PostMapping("/registration")
    public String createUserAccount(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String password) {
        String encodedPassword = passwordEncoder.encode(password);
        userService.save(new User(name, email, encodedPassword, UserRole.USER));
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        SecurityContextHolder.clearContext();

        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return "redirect:/";
    }
}
