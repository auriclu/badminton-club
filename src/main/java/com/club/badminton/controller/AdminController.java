package com.club.badminton.controller;

import com.club.badminton.dao.UserDao;
import com.club.badminton.model.*;
import com.club.badminton.service.EventRegistrationService;
import com.club.badminton.service.EventService;
import com.club.badminton.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Controller for admin features
 * Handles event, user, inventory management
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EventRegistrationService eventRegistrationService;
    private final UserDao userDao;
    private final EventService eventService;
    private final UserService userService;

    public AdminController(EventRegistrationService eventRegistrationService,
                           UserDao userDao,
                           EventService eventService,
                           UserService userService) {
        this.eventRegistrationService = eventRegistrationService;
        this.userDao = userDao;
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Ensures that only Admins can access these pages
     * @param session
     * @return
     */
    private String checkAdminAccess(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() == Role.GUEST || user.getRole() == Role.MEMBER) return "redirect:/member/events";
        if (user.getRole() == Role.COACH) return "redirect:/coach/sessions";
        return null;
    }

    /**
     * Display main admin dashboard with registration statistics and pending requests
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        List<EventRegistration> regs = eventRegistrationService.findAll();
        List<Event> events = eventService.findAll();

        Map<Integer, String> eventNames = events.stream()
                .collect(Collectors.toMap(Event::getEventId, Event::getName));

        model.addAttribute("registrations", regs);
        model.addAttribute("totalCount", regs.size());
        model.addAttribute("pendingCount", regs.stream().filter(r -> RegistrationStatus.PENDING.equals(r.getStatus())).count());
        model.addAttribute("racketRentalCount", regs.stream().filter(EventRegistration::isNeedsRacket).count());
        model.addAttribute("eventNames", eventNames);

        return "admin/dashboard";
    }

    /**
     * List all registered users
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        model.addAttribute("users", userDao.findAll());
        return "admin/user-list";
    }

    /**
     * Promote/demote user's role
     * @param session
     * @param userId
     * @param newRole
     * @return
     */
    @PostMapping("/promote")
    public String promoteUser(HttpSession session,
                              @RequestParam("userId") int userId,
                              @RequestParam("newRole") Role newRole) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        User user = userDao.findById(userId).orElseThrow();
        user.setRole(newRole);
        userDao.update(user);

        return "redirect:/admin/users";
    }

    /**
     * Submit and save updates to a member profile
     * @param session
     * @param userId
     * @param role
     * @return
     */
    @PostMapping("/update-member")
    public String updateMember(HttpSession session,
                               @RequestParam int userId,
                               @RequestParam Role role) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        User user = userDao.findById(userId).orElseThrow();
        user.setRole(role);
        userDao.update(user);

        return "redirect:/admin/users?success";
    }

    /**
     * Cancel (reject) registration request
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/cancel/{id}")
    public String cancelRegistration(@PathVariable("id") int id, HttpSession session) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        eventRegistrationService.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Approve a registration request
     * @param id
     * @param session
     * @return
     */
    @GetMapping("/approve/{id}")
    public String approveRegistration(@PathVariable("id") int id, HttpSession session) {
        String authRedirect = checkAdminAccess(session);
        if (authRedirect != null) return authRedirect;

        EventRegistration reg = eventRegistrationService.findById(id);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.APPROVED);
            eventRegistrationService.save(reg);
        }
        return "redirect:/admin/dashboard";
    }
}