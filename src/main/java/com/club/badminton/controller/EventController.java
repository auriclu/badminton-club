package com.club.badminton.controller;

import com.club.badminton.dao.EventDao;
import com.club.badminton.dto.EventRegistrationForm;
import com.club.badminton.exception.BusinessValidationException;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventRegistration;
import com.club.badminton.model.RegistrationStatus;
import com.club.badminton.model.User;
import com.club.badminton.service.EventRegistrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user registrations for events
 */
@Controller
@RequestMapping("/events")
public class EventController {

    private final EventDao eventDao;
    private final EventRegistrationService registrationService;

    public EventController(EventDao eventDao, EventRegistrationService registrationService) {
        this.eventDao = eventDao;
        this.registrationService = registrationService;
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    /**
     * Displays event registration form
     * @param eventId
     * @param model
     * @return
     */
    @GetMapping("/register/{eventId}")
    public String showRegistrationForm(@PathVariable("eventId") int eventId, HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login?error=unauthorized";
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + eventId));

        EventRegistrationForm form = new EventRegistrationForm();
        form.setEventId(eventId);

        model.addAttribute("event", event);
        model.addAttribute("registrationForm", form);
        return "events/register";
    }

    /**
     * Process form submissions
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registrationForm") EventRegistrationForm form,
                                      BindingResult bindingResult,
                                      HttpSession session,
                                      Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login?error=unauthorized";
        }

        Event event = eventDao.findById(form.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + form.getEventId()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            return "events/register";
        }

        EventRegistration reg = new EventRegistration();
        reg.setEventId(form.getEventId());
        reg.setUserId(currentUser.getUserId());
        reg.setGuestName(form.getGuestName());
        reg.setGuestEmail(form.getGuestEmail());
        reg.setGuestPhone(form.getGuestPhone());
        reg.setSkillLevel(form.getSkillLevel());
        reg.setNeedsRacket(form.isNeedsRacket());
        reg.setPartnerName(form.getPartnerName());
        reg.setStatus(RegistrationStatus.PENDING);

        try {
            EventRegistration savedReg = registrationService.registerForEvent(reg, event, 1);
            return "redirect:/events/status?id=" + savedReg.getRegistrationId();
        } catch (BusinessValidationException ex) {
            model.addAttribute("businessError", ex.getMessage());
            model.addAttribute("event", event);
            return "events/register";
        }
    }

    /**
     * Allows a user to check the status
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/status")
    public String checkStatus(@RequestParam(value = "id") Integer id, HttpSession session, Model model) {
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login?error=unauthorized";
        }

        EventRegistration reg = registrationService.findById(id);

        model.addAttribute("registration", reg);
        return "redirect:/member/events";
    }
}