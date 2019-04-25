package se.kth.moadb.haxonomysite.presentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import se.kth.moadb.haxonomysite.application.taxonomy.SecurityService;
import se.kth.moadb.haxonomysite.domain.User;

import java.security.Principal;

/**
 * Entry point for REST requests concerning authentication such as registration.
 * Implemented methods:
 * - POST /registration, create a new user and person.
 */
@RestController
public class AuthenticationController {

    @Autowired
    SecurityService securityService;


    /**
     * Register a new user and person.
     * JSON data should be structured as follows:
     * {
     *    email : String,
     *    password : String,
     * }
     */
    @PostMapping("/registration")
    public User registerUserAndPerson(@RequestBody RegistrationPostRequest registrationPostRequest) {
        return securityService.saveUser(registrationPostRequest.getEmail(), registrationPostRequest.getPassword(), "taxonomist");
    }


    /**
     * Redirected to on successful login
     * @param model
     * @param principal, of the logged in user
     * @return user details of the logged in user.
     */
    @GetMapping("/login/success")
    public User successLogin(ModelMap model, Principal principal){
        return securityService.getUser(principal.getName());
    }
}
