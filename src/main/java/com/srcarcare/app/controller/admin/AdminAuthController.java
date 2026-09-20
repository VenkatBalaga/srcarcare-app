package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.AdminUser;
import com.srcarcare.app.repository.AdminUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthController(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("active", "change-password");
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                  @RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirectAttributes) {
        AdminUser admin = adminUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Admin user not found"));

        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/admin/change-password";
        }
        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long.");
            return "redirect:/admin/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match.");
            return "redirect:/admin/change-password";
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(admin);
        redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
        return "redirect:/admin/change-password";
    }
}
