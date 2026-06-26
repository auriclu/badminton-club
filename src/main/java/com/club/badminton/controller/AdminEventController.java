package com.club.badminton.controller;

import com.club.badminton.exception.InvalidDateException;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventType;
import com.club.badminton.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing the creation, listing and deletion of club events by Admin
 */
@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Display the form to create a new event
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("eventTypes", EventType.values());
        return "admin/create-event";
    }

    /**
     * Process creation of a new event
     * Capture exceptions and return error feedback to the view
     * @param event
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/new")
    public String createEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        try {
            eventService.save(event);
            return "redirect:/admin/events?success";
        } catch (InvalidDateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/events/new";
        }
    }

    /**
     * List all current and past events
     * @param model
     * @return
     */
    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.findAll());
        return "admin/manage-events";
    }

    /**
     * Delete an event from the system
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") int id) {
        eventService.deleteById(id);
        return "redirect:/admin/events";
    }
}