package com.anicare.project.controller;

import com.anicare.project.model.City;
import com.anicare.project.model.Customer;
import com.anicare.project.service.CityService;
import com.anicare.project.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class CityController {

    private final CustomerService customerService;
    private final CityService cityService;

    @Autowired
    public CityController(CustomerService customerService, CityService cityService) {
        this.customerService = customerService;
        this.cityService = cityService;
    }

    @RequestMapping(value = "/changeCity", method = RequestMethod.GET)
    public String changeCity(Model model) {

        model.addAttribute("customer", new Customer());
        model.addAttribute("cities", cityService.allCities().stream().map(City::getCity).collect(Collectors.toList()));
        return "changeCity";
    }

    @RequestMapping(value = "/changeCity", method = RequestMethod.POST)
    public String doChangeCity(@RequestParam("city") final String city, Principal principal, final RedirectAttributes redirect) {
        if (city.equals("0"))
            return "redirect:/welcome";
        Customer customer = customerService.findByUsername(principal.getName());
        customer.setCity(city);
        customerService.updateCustomer(customer);
        redirect.addFlashAttribute("success", true);
        return "redirect:/welcome";
    }
}
