package com.example.productcrud.controller;

import com.example.productcrud.dto.ChangePasswordDTO;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getUser();
            } else if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    private void refreshAuthentication(User user) {
        User fromDb = userRepository.findById(user.getId()).orElseThrow();
        CustomUserDetails updated = new CustomUserDetails(fromDb);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updated, null, updated.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @GetMapping
    public String viewProfile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Profil");
        return "profile/view-profile";
    }

    @GetMapping("/edit")
    public String editForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit profil");
        return "profile/edit-profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        model.addAttribute("pageTitle", "Ubah sandi");
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDTO dto, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password lama tidak sesuai.");
            return "redirect:/profile/change-password";
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru dan konfirmasi tidak cocok.");
            return "redirect:/profile/change-password";
        }
        if (dto.getNewPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password minimal 6 karakter.");
            return "redirect:/profile/change-password";
        }
        if (dto.getNewPassword().equals(dto.getOldPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru harus berbeda dari password lama.");
            return "redirect:/profile/change-password";
        }

        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(currentUser);
        refreshAuthentication(currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah.");
        return "redirect:/profile";
    }

    @PostMapping("/edit")
    public String editSubmit(
            @ModelAttribute User formUser,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes) throws IOException {

        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }
        user = userRepository.findById(user.getId()).orElseThrow();

        if (StringUtils.hasText(formUser.getEmail())) {
            String trimmedEmail = formUser.getEmail().trim();
            Optional<User> emailOwner = userRepository.findByEmail(trimmedEmail);
            if (emailOwner.isPresent() && !emailOwner.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email sudah digunakan akun lain.");
                return "redirect:/profile/edit";
            }
            user.setEmail(trimmedEmail);
        } else {
            user.setEmail(null);
        }

        if (StringUtils.hasText(formUser.getFullName())) {
            user.setFullName(formUser.getFullName().trim());
        }
        user.setPhoneNumber(StringUtils.hasText(formUser.getPhoneNumber()) ? formUser.getPhoneNumber().trim() : null);
        user.setAddress(StringUtils.hasText(formUser.getAddress()) ? formUser.getAddress().trim() : null);
        user.setBio(StringUtils.hasText(formUser.getBio()) ? formUser.getBio().trim() : null);

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
        refreshAuthentication(user);

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
