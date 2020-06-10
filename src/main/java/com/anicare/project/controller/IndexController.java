package com.anicare.project.controller;

import com.anicare.project.model.*;
import com.anicare.project.service.CityService;
import com.anicare.project.service.CustomerService;
import com.anicare.project.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.IntStream;

@Controller
public class IndexController {

    private final CustomerService customerService;
    private final CityService cityService;
    private final ShelterService shelterService;

    @Autowired
    public IndexController(CustomerService customerService, CityService cityService, ShelterService shelterService) {
        this.customerService = customerService;
        this.cityService = cityService;
        this.shelterService = shelterService;
    }

    @RequestMapping(value = "/welcome")
    public String welcome() {

        return "home";
    }

    @RequestMapping(value = "/forgotPassword")
    public String forgotPassword(Model model) {
        model.addAttribute("customer", new Customer());

        List<String> securityQuestions = getSecurityQuestions();
        model.addAttribute("securityQuestions", securityQuestions);

        return "forgotPassword";
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public String forgotPassword(@RequestParam("email") final String email,
                                 @RequestParam("securityQuestion") final String securityQuestion,
                                 @RequestParam("securityAnswer") final String securityAnswer,
                                 Model model) {
        model.addAttribute("customer", new Customer());

        List<String> securityQuestions = getSecurityQuestions();
        model.addAttribute("securityQuestions", securityQuestions);

        Customer customerFound = customerService.findByEmail(email);

        if (customerFound == null) {
            model.addAttribute("isFailed", true);
            return "forgotPassword";
        }

        if (customerFound.getSecurityQuestion().equalsIgnoreCase(securityQuestion)
                && customerFound.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(customerFound, null, customerFound.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            return "changePassword";
        }

        model.addAttribute("isFailed", true);
        return "forgotPassword";
    }

    @RequestMapping(value = "/shelter")
    public String shelter(Model model) {
        List<Shelter> shelter = shelterService.allShelters();
        Collections.sort(shelter);
        IntStream.range(0, shelter.size())
                .forEachOrdered(i -> shelter.get(i).setId((long) i + 1));
        model.addAttribute("shelters", shelter);
        return "shelter";
    }

    @RequestMapping(value = "/redirectShelter")
    public String redirectShelter(String webAddress) {
        return "redirect://" + webAddress;
    }

    @RequestMapping(value = {"/home", "/"})
    public String index(Principal principal) {
        if (principal == null)
            return "index";
        return "home";
    }

    @RequestMapping(value = "/contact")
    public String contact() {
        return "contact";
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth == null))
            new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/home";
    }

    @RequestMapping(value = "/registerVet")
    public String registerVet(Model model) {
        model.addAttribute("customer", new Customer());

        List<String> securityQuestions = getSecurityQuestions();
        model.addAttribute("securityQuestions", securityQuestions);

        List<City> cities = cityService.allCities();
        model.addAttribute("cities", cities);
        return "registerVet";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {

        model.addAttribute("customer", new Customer());

        List<String> securityQuestions = getSecurityQuestions();
        model.addAttribute("securityQuestions", securityQuestions);

        List<City> cities = cityService.allCities();
        model.addAttribute("cities", cities);
        return "register";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(Model model) {

        model.addAttribute("customer", new Customer());
        model.addAttribute("password", "");
        return "changePassword";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(@RequestParam("password") final String password,
                                 Principal principal,
                                 final RedirectAttributes redirect) {
        Customer customer = customerService.findByUsername(principal.getName());
        customer.setPassword(password);
        customerService.saveCustomer(customer, customer.getCustomerRoles());
        redirect.addFlashAttribute("success", true);
        return "redirect:/welcome";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String doRegister(@Valid Customer customer,
                             BindingResult result,
                             Model model,
                             final RedirectAttributes redirect) {

        model.addAttribute("cities", cityService.allCities());

        if (customerService.checkUserExist(customer.getUsername(), customer.getEmail())) {
            if (customerService.checkEmailExist(customer.getEmail())) {
                model.addAttribute("emailExist", true);
            }
            if (customerService.checkUsernameExist(customer.getUsername())) {
                model.addAttribute("usernameExist", true);
            }
            if (customer.getCity().equals("")) {
                model.addAttribute("cityEmpty", true);
            }

            return "register";
        }

        if (customer.getCity().equals("")) {
            model.addAttribute("cityEmpty", true);
            return "register";
        }

        if (!(result.hasErrors())) {
            Set<CustomerRole> customerRoles = new HashSet<>();
            customerRoles.add(new CustomerRole(customer, new Role().getDefaultRole()));

            customer.setEnabled(true);
            customer.setType("U");

            customerService.saveCustomer(customer, customerRoles);
            redirect.addFlashAttribute("success", true);
            return "redirect:/login";
        } else return "register";
    }

    private List<String> getSecurityQuestions() {
        ArrayList<String> securityQuestions = new ArrayList<>();

        securityQuestions.add("First pet's name? (Security Question)");
        securityQuestions.add("Mom's maiden name?");
        securityQuestions.add("Primary school teacher's name?");

        return securityQuestions;
    }
}
