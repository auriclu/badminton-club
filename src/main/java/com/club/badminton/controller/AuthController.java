package com.club.badminton.controller;

import com.club.badminton.dao.UserDao;
import com.club.badminton.dto.SignupForm;
import com.club.badminton.model.User;
import com.club.badminton.model.Role;
import com.club.badminton.util.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Hangles Signup, Login, Logout
 * Manages user session creation, role based redirecting after authentication
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserDao userDao;

    public AuthController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Display user registration form
     * @param model
     * @return
     */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }

    /**
     * Process user registration, hashes password, assigns default Guest role
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") SignupForm form, Model model) {
        if (userDao.findByEmail(form.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            return "auth/signup";
        }

        User user = new User();
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setRole(Role.GUEST);
        user.setCreatedAt(LocalDateTime.now());

        user.setPasswordHash(PasswordUtils.hashPassword(form.getPassword()));

        try {
            userDao.save(user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "auth/signup";
        }

        return "redirect:/auth/login?registered=true";
    }

    /**
     * Display login page and parse URL parameters
     * @param error
     * @param registered
     * @param logout
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registered", required = false) String registered,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {

        // Handle URL parameters for UI feedback
        if ("unauthorized".equals(error)) {
            model.addAttribute("errorMessage", "Please log in first to access that page.");
        } else if ("forbidden".equals(error)) {
            model.addAttribute("errorMessage", "Access denied. You don't have permission to view that resource.");
        } else if ("bad_credentials".equals(error)) {
            model.addAttribute("errorMessage", "Invalid email or password.");
        } else if ("true".equals(registered)) {
            model.addAttribute("successMessage", "Account created successfully! Please log in.");
        } else if ("true".equals(logout)) {
            model.addAttribute("successMessage", "You have been successfully logged out.");
        }

        return "auth/login";
    }

    /**
     * Authenticates user credentials, establishes HTTP session
     * Redirects to the appropriate page according to their role
     * @param email
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              HttpSession session) {

        Optional<User> userOpt = userDao.findByEmail(email);

        if (userOpt.isPresent() && PasswordUtils.checkPassword(password, userOpt.get().getPasswordHash())) {
            User user = userOpt.get();

            session.setAttribute("currentUser", user);

            return switch (user.getRole()) {
                case ADMIN -> "redirect:/admin/dashboard";
                case COACH -> "redirect:/coach/sessions";
                case MEMBER -> "redirect:/member/profile";
                case GUEST -> "redirect:/member/events";
            };
        }

        return "redirect:/auth/login?error=bad_credentials";
    }

    /**
     * Log out and invalidate the active HTTP session
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/auth/login?logout=true";
    }
}