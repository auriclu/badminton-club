package com.club.badminton.controller;

import com.club.badminton.model.*;
import com.club.badminton.service.EventService;
import com.club.badminton.service.EventRegistrationService;
import com.club.badminton.service.AttendanceService; // Make sure this is imported!
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages access to personal dashboard, attendance history, and club events list
 */
@Controller
@RequestMapping("/member")
public class MemberController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final AttendanceService attendanceService; // 1. Declare the new service

    public MemberController(EventService eventService,
                            EventRegistrationService registrationService,
                            AttendanceService attendanceService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }


    /**
     * Guides user to appropriate starting page according to their role
     * @param session
     * @return
     */
    @GetMapping("/dashboard")
    public String defaultDashboardRedirect(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/auth/login";

        if (user.getRole() == Role.GUEST) return "redirect:/member/events";
        return "redirect:/member/profile";
    }

    /**
     * Display available upcoming events and active registrations
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/events")
    public String showEvents(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/auth/login";

        if (user.getRole() != Role.MEMBER && user.getRole() != Role.GUEST) {
            return "redirect:/auth/login?error=forbidden";
        }


        List<Event> allEvents = eventService.findAllUpcoming();
        List<EventRegistration> registrations = registrationService.findAll();

        Map<Integer, String> eventNameMap = allEvents.stream()
                .collect(Collectors.toMap(Event::getEventId, Event::getName));

        model.addAttribute("availableEvents", allEvents);
        model.addAttribute("myRegistrations", registrations);
        model.addAttribute("eventNameMap", eventNameMap);

        return "member/events";
    }

    /**
     * Personal profile view for fully registered Members
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/auth/login";

        // RESTRICTED: Bounce guests back to the events pool
        if (user.getRole() == Role.GUEST) return "redirect:/member/events";
        if (user.getRole() != Role.MEMBER) return "redirect:/auth/login?error=forbidden";

        model.addAttribute("user", user);
        model.addAttribute("clubTitle", "Club Treasurer");
        model.addAttribute("responsibilities", List.of(
                "Managing seasonal club operational budgets.",
                "Procuring technical court gear (Match-grade shuttlecocks and training rackets).",
                "Auditing monthly membership subscription ledgers."
        ));
        return "member/profile";
    }

    /**
     * Calculate and display attendance statistics for a Member
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/attendance")
    public String showAttendance(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/auth/login";

        // RESTRICTED: Bounce guests back to the events pool
        if (user.getRole() == Role.GUEST) return "redirect:/member/events";
        if (user.getRole() != Role.MEMBER) return "redirect:/auth/login?error=forbidden";

        // Querying data via TrainingAttendance signature structures
        List<TrainingAttendance> attendanceList = attendanceService.findByUserId(user.getUserId());

        // Dynamic Calculations based on your status rules
        long presentCount = attendanceList.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
        long absentCount = attendanceList.stream().filter(r -> "ABSENT".equalsIgnoreCase(r.getStatus())).count();
        long pendingCount = attendanceList.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();

        long totalEvaluated = presentCount + absentCount;

        double attendanceRate = (totalEvaluated > 0) ? ((double) presentCount / totalEvaluated) * 100 : 0.0;
        double absentRate = (totalEvaluated > 0) ? ((double) absentCount / totalEvaluated) * 100 : 0.0;

        // Binding to Model Context
        model.addAttribute("attendanceList", attendanceList); // Passes individual sessions
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("attendancePercentage", String.format("%.1f", attendanceRate)); // Overall rate
        model.addAttribute("absentPercentage", String.format("%.1f", absentRate));

        return "member/attendance";
    }
}