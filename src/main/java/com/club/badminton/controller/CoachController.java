package com.club.badminton.controller;

import com.club.badminton.model.Role;
import com.club.badminton.model.TrainingAttendance;
import com.club.badminton.model.User;
import com.club.badminton.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Handles creation of training sessions and updating player attendance
 */
@Controller
@RequestMapping("/coach")
public class CoachController {

    private final AttendanceService attendanceService;

    public CoachController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    private boolean isCoach(User user) {
        return user != null && user.getRole() == Role.COACH;
    }

    /**
     * Display overview of all training sessions
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/sessions")
    public String showSessions(HttpSession session, Model model) {
        if (!isCoach(getCurrentUser(session))) return "redirect:/auth/login?error=forbidden";

        List<LocalDate> sessionDates = attendanceService.findAllSessionDates();
        model.addAttribute("sessionDates", sessionDates);

        return "coach/sessions";
    }

    /**
     * Initialize a new training sessions for a given date
     * All active members' default status is Pending
     * @param dateStr
     * @param session
     * @return
     */
    @PostMapping("/sessions/new")
    public String createSession(@RequestParam("trainingDate") String dateStr, HttpSession session) {
        if (!isCoach(getCurrentUser(session))) return "redirect:/auth/login?error=forbidden";

        LocalDate date = LocalDate.parse(dateStr);
        // This leverages your existing method to batch-insert PENDING rows for all active members
        attendanceService.initializeSessionForDate(date);

        return "redirect:/coach/sessions/" + dateStr;
    }

    /**
     * Display editable attendance for a specific training date
     * @param dateStr
     * @param updated
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/sessions/{date}")
    public String showRoster(@PathVariable("date") String dateStr,
                             @RequestParam(value = "updated", required = false) String updated,
                             HttpSession session, Model model) {
        if (!isCoach(getCurrentUser(session))) return "redirect:/auth/login?error=forbidden";

        List<TrainingAttendance> roster = attendanceService.findByDate(LocalDate.parse(dateStr));

        if ("true".equals(updated)) {
            model.addAttribute("successMessage", "Attendance successfully updated.");
        }

        model.addAttribute("roster", roster);
        model.addAttribute("sessionDate", dateStr);

        return "coach/roster";
    }

    /**
     * Process batch updates
     * @param dateStr
     * @param allParams
     * @param session
     * @return
     */
    @PostMapping("/sessions/{date}/update")
    public String updateRoster(@PathVariable("date") String dateStr,
                               @RequestParam Map<String, String> allParams,
                               HttpSession session) {
        if (!isCoach(getCurrentUser(session))) return "redirect:/auth/login?error=forbidden";

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("status_")) {
                int attendanceId = Integer.parseInt(entry.getKey().replace("status_", ""));
                String status = entry.getValue();

                attendanceService.updateSingleStatus(attendanceId, status);
            }
        }

        return "redirect:/coach/sessions/" + dateStr + "?updated=true";
    }
}