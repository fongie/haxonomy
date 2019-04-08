package se.kth.moadb.haxonomysite.presentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import se.kth.moadb.haxonomysite.application.SecurityService;
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

    /*
    @Autowired
    PersonService personService;
    */

    /**
     * Register a new user and person.
     * JSON data should be structured as follows:
     * {
     *    email : String,
     *    password : String,
     * }
     */
    /*
    @PostMapping("/registration")
    public Person registerUserAndPerson(@RequestBody RegistrationPostRequest registrationPostRequest) {
        Person person = personService.savePersonAndUser(registrationPostRequest);
        return person;
    }
    */

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
