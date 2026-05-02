package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");
    private static final long MAX_IMAGE_BYTES = 2 * 1024 * 1024;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.profiles-dir:uploads/profiles}")
    private String profilesUploadDir;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @GetMapping("/password")
    public String changePasswordForm() {
        return "profile-password";
    }

    @PostMapping("/password")
    public String changePasswordSubmit(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password saat ini salah.");
            return "redirect:/profile/password";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru dan konfirmasi tidak cocok.");
            return "redirect:/profile/password";
        }
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru minimal 6 karakter.");
            return "redirect:/profile/password";
        }
        if (newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru harus berbeda dari password saat ini.");
            return "redirect:/profile/password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah.");
        return "redirect:/profile";
    }

    @PostMapping("/edit")
    public String editSubmit(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String bio,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes) throws IOException {

        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();

        if (StringUtils.hasText(email)) {
            String trimmedEmail = email.trim();
            Optional<User> emailOwner = userRepository.findByEmail(trimmedEmail);
            if (emailOwner.isPresent() && !emailOwner.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email sudah digunakan akun lain.");
                return "redirect:/profile/edit";
            }
            user.setEmail(trimmedEmail);
        } else {
            user.setEmail(null);
        }

        if (StringUtils.hasText(fullName)) {
            user.setFullName(fullName.trim());
        }
        user.setPhoneNumber(StringUtils.hasText(phoneNumber) ? phoneNumber.trim() : null);
        user.setAddress(StringUtils.hasText(address) ? address.trim() : null);
        user.setBio(StringUtils.hasText(bio) ? bio.trim() : null);

        if (profileImage != null && !profileImage.isEmpty()) {
            String contentType = profileImage.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Gunakan gambar JPG, PNG, GIF, atau WebP.");
                return "redirect:/profile/edit";
            }
            if (profileImage.getSize() > MAX_IMAGE_BYTES) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ukuran gambar maksimal 2 MB.");
                return "redirect:/profile/edit";
            }
            String suffix = extensionForMime(contentType);
            String filename = UUID.randomUUID() + suffix;
            Path dir = Paths.get(profilesUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            try (InputStream in = profileImage.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            user.setProfileImageUrl("/uploads/profiles/" + filename);
        }

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Profil berhasil diperbarui.");
        return "redirect:/profile";
    }

    private static String extensionForMime(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
