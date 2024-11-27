package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/page")
    public String adminPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("principal", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @PostMapping("/redactor/{id}")
    public String patchAdminRedactor(@ModelAttribute("user") User user, @PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.adminRedactor(user, id);
        return "redirect:/admin/page";
    }

    @PostMapping("/delete/{id}")
    public String adminDelete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.delete(id);
        return "redirect:/admin/page";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute("newUser") User newUser, @AuthenticationPrincipal UserDetails userDetails,
                                   BindingResult bindingResult, Model model) {
        User existingUser = userService.getUserByEmail(newUser.getEmail());
        if (existingUser != null) {
            bindingResult.rejectValue("email", "email.exists", "There is already an account registered with the same email");
        }
        if (bindingResult.hasErrors()) {
            User user = userService.getUserByEmail(userDetails.getUsername());
            model.addAttribute("principal", user);
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("newUser", newUser);
            model.addAttribute("hasErrors", true);
            return "/admin";
        }
        userService.saveUser(newUser);
        return "redirect:/admin/page";
    }
}
