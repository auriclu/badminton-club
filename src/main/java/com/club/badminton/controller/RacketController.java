package com.club.badminton.controller;

import com.club.badminton.model.Racket;
import com.club.badminton.service.RacketService;
import com.club.badminton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Tracks club inventory and assigns club rackets
 */
@Controller
@RequestMapping("/admin/rackets")
public class RacketController {

    private final RacketService racketService;
    private final UserService userService;

    public RacketController(RacketService racketService, UserService userService) {
        this.racketService = racketService;
        this.userService = userService;
    }

    /**
     * Main racket inventory dashboard
     * @param model
     * @return
     */
    @GetMapping
    public String viewRacketsHub(Model model) {
        model.addAttribute("rackets", racketService.getAllRackets());
        model.addAttribute("newRacket", new Racket()); // Crucial for th:object="${newRacket}"
        model.addAttribute("members", userService.findAllActiveMembers());
        return "admin/rackets";
    }

    /**
     * Adds a new racket to the database
     * @param racket
     * @return
     */
    @PostMapping("/add")
    public String addRacket(@ModelAttribute("newRacket") Racket racket) {
        racketService.addNewRacket(racket);
        return "redirect:/admin/rackets";
    }

    /**
     * Updates the status of a given racket
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable("id") int id, @RequestParam("status") String status) {
        racketService.updateRacketStatus(id, status);
        return "redirect:/admin/rackets";
    }

    /**
     * Permanently assigns a racket to a registered club member
     * @param id
     * @param memberId
     * @return
     */
    @PostMapping("/{id}/assign")
    public String assignPermanent(@PathVariable("id") int id, @RequestParam("memberId") int memberId) {
        racketService.assignPermanentRacket(id, memberId);
        return "redirect:/admin/rackets";
    }
}