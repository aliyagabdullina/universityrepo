package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.data.User;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        System.out.println(model.asMap());
        if (error != null) {
            model.addAttribute("param", Map.of("error", true));
        }
        return "authorisation";
    }


    @PostMapping("/login")
    public String loginSubmit(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.saveUser(user);
        return "redirect:/account";
    }

    @GetMapping("/authorisation")
    public String authorisationPage() {
        return "authorisation";
    }

//    @PostMapping("/authorise")
//    public String authorisationSubmit(@RequestParam("username") String username, @RequestParam("password") String password) {
//        int ifRightPassword = userService.authenticate(username, password);
//        if (ifRightPassword == 0) {
//            return "redirect:/authorise";
//        }
//        if (ifRightPassword == 1) {
//            return "redirect:/authorise";
//        }
//
//        return "redirect:/account";
//    }
}
