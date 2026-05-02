package com.example.productcrud.controller;

import com.example.productcrud.dto.ChangePasswordDTO;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping
    public String viewProfile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("pageTitle", "Profile");
        return "profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        model.addAttribute("pageTitle", "Change Password");
        return "profile-change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDTO changePasswordDTO,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password lama tidak sesuai!");
            return "redirect:/profile/change-password";
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru dan konfirmasi password tidak cocok!");
            return "redirect:/profile/change-password";
        }

        if (changePasswordDTO.getNewPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password minimal 6 karakter!");
            return "redirect:/profile/change-password";
        }

        currentUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(currentUser);

        CustomUserDetails updatedUserDetails = new CustomUserDetails(currentUser);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah!");
        return "redirect:/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", currentUser);
        return "profile-edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute User user,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        currentUser.setFullName(user.getFullName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setAddress(user.getAddress());
        currentUser.setBio(user.getBio());

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String mimeType = profileImage.getContentType();
                if (mimeType == null || !mimeType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "File harus berupa gambar!");
                    return "redirect:/profile/edit";
                }
                if (profileImage.getSize() > 2 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Ukuran gambar maksimal 2MB!");
                    return "redirect:/profile/edit";
                }
                byte[] imageBytes = profileImage.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                currentUser.setProfileImageUrl("data:" + mimeType + ";base64," + base64Image);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengunggah gambar: " + e.getMessage());
                return "redirect:/profile/edit";
            }
        }

        userRepository.save(currentUser);

        CustomUserDetails updatedUserDetails = new CustomUserDetails(currentUser);
        Authentication newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }
}