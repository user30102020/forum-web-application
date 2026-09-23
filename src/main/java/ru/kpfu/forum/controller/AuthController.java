package ru.kpfu.forum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kpfu.forum.dto.RegisterForm;
import ru.kpfu.forum.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterForm registerForm, Model model) {
        try {
            userService.register(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword());
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorCode", exception.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
