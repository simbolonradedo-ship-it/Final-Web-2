package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"));
    }

    @GetMapping("/auth/login")
    public String loginPage(Model model, String error, String logout) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau password salah!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Anda telah berhasil logout.");
        }
        return "login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                          @RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Password dan konfirmasi password tidak cocok!");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password minimal 6 karakter!");
            return "register";
        }

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("errorMessage", "Username sudah digunakan!");
            return "register";
        }

        if (email != null && !email.trim().isEmpty() && userRepository.existsByEmail(email)) {
            model.addAttribute("errorMessage", "Email sudah digunakan!");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setEnabled(true);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", 
            "Registrasi berhasil! Silakan login dengan akun Anda.");
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/logout")
    public String logout() {
        return "redirect:/auth/login?logout=true";
    }
}
