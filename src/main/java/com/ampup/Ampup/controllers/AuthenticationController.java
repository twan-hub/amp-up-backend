package com.ampup.Ampup.controllers;

import com.ampup.Ampup.models.DTO.LoginFormDTO;
import com.ampup.Ampup.models.DTO.RegisterFormDTO;
import com.ampup.Ampup.models.User;
import com.ampup.Ampup.models.data.UserRepository;
import com.ampup.Ampup.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public class  AuthenticationController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private static final String userSessionKey="user";

    @PostMapping("/register")
    public String processRegistrationForm(@RequestBody @Valid RegisterFormDTO registerFormDTO,
                                          Errors errors, HttpServletRequest request,
                                          Model model) {

        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());

        if (existingUser != null) {
            return "Exist";
        }

        String password = registerFormDTO.getPassword();
        String verifyPassword = registerFormDTO.getVerifyPassword();
        if (!password.equals(verifyPassword)) {
            return "Password";
        }

        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        userRepository.save(newUser);
//        authenticationService.setUserInSession(request.getSession(), newUser);

        return "Success";
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public String processLoginForm(@RequestBody @Valid LoginFormDTO loginFormDTO,
                                   Errors errors,HttpServletRequest request,
                                   Model model) {

//        if (errors.hasErrors()) {
//            model.addAttribute("title", "Log In");
//            return "";
//        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());
        String password = loginFormDTO.getPassword();
        if (theUser == null||!theUser.isMatchingPassword(password)) {
//            errors.rejectValue("username", "user.invalid", "Invalid Username or Password");
//            model.addAttribute("title", "Log In");
            return "Invalid";
        }



        if (!theUser.isMatchingPassword(password)) {
//            errors.rejectValue("password", "password.invalid", "Invalid Username or Password");
//            model.addAttribute("title", "Log In");
            return "Invalid";
        }

        authenticationService.setUserInSession(request.getSession(), theUser);

        return "Success";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/login";
    }
}