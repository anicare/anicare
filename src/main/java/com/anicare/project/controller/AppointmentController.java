package com.anicare.project.controller;

import com.anicare.project.model.*;
import com.anicare.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final VetService vetService;
    private final PetService petService;
    private final CityService cityService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 CustomerService customerService,
                                 VetService vetService,
                                 PetService petService,
                                 CityService cityService) {
        this.appointmentService = appointmentService;
        this.vetService = vetService;
        this.petService = petService;
        this.cityService = cityService;
        this.customerService = customerService;
    }

    @RequestMapping(value = "/appointments", method = RequestMethod.GET)
    public ModelAndView appointments(Model model, Principal principal) {
        model.addAttribute("appointment", new Appointment());

        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("customer", customer);

        List<Appointment> appointments = appointmentService.allAppointments();
        List<Appointment> appointmentsByCustomer = getFilteredAppointments(customer, appointments);

        return new ModelAndView("appointments", "appointments", appointmentsByCustomer);
    }

    @RequestMapping(value = "/vets", method = RequestMethod.GET)
    public ModelAndView vets() {
        List<Vet> vets = vetService.allVets();
        return new ModelAndView("vets", "vets", vets);
    }

    @RequestMapping(value = "/newAppointment", method = RequestMethod.GET)
    public ModelAndView newAppointment(Model model, Principal principal) {
        model.addAttribute("appointment", new Appointment());

        List<Vet> vets = vetService.allVets();

        List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
        model.addAttribute("pets", pets);

        List<City> cities = cityService.allCities();
        model.addAttribute("cities", cities);
        model.addAttribute("times", getTimes());

        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("customer", customer);

        List<Vet> vetsFiltered = getFilteredAppointments(vets, customer);
        return new ModelAndView("newAppointment", "vets", vetsFiltered);
    }

    @RequestMapping(value = "/saveNewAppointment", method = RequestMethod.POST)
    public ModelAndView saveNewAppointment(@Valid Appointment newAppointment,
                                           BindingResult result,
                                           Model model,
                                           Principal principal,
                                           final RedirectAttributes redirect) {

        Date toDate = newAppointment.getToDate();
        String toTime = newAppointment.getToTime();
        Vet vet = newAppointment.getVet();

        model.addAttribute("notAvailable", false);
        model.addAttribute("timeRequired", false);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(toDate);

        boolean exists = false;
        List<String> availableTimes = getTimes();
        List<Appointment> appointments = appointmentService.allAppointments();

        for (Appointment appointment : appointments) {
            if (appointment.getToDate().toString().equals(formattedDate) && appointment.getVet().equals(vet)) {
                if (appointment.getToTime().equals(toTime)) {
                    availableTimes.remove(appointment.getToTime());
                    exists = true;
                }
            }
        }

        if (toTime.equals("0")) {
            String timeRequiredString = "Time is required.";

            List<Vet> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);
            List<Vet> vetsFiltered = getFilteredAppointments(vets, customer);

            model.addAttribute("timeRequired", true);
            model.addAttribute("timeRequiredString", timeRequiredString);
            model.addAttribute("times", availableTimes);
            return new ModelAndView("newAppointment", "vets", vetsFiltered);
        }

        if (exists) {
            String availableTimesString = "";

            List<Vet> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);

            List<Vet> vetsFiltered = getFilteredAppointments(vets, customer);
            availableTimesString += "Selected time (" + toTime + ")" + " is not available.";
            if (availableTimes.isEmpty()) {
                availableTimesString += "<br/>There are no available times for this date for this vet.";
            } else {
                availableTimesString += "<br/>You can choose available times with the new list down below.";
            }
            model.addAttribute("notAvailable", true);
            model.addAttribute("timeList", availableTimesString);
            model.addAttribute("times", availableTimes);
            return new ModelAndView("newAppointment", "vets", vetsFiltered);
        }

        if (result.hasErrors()) {
            List<Vet> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);

            List<Vet> vetsFiltered = getFilteredAppointments(vets, customer);
            model.addAttribute("times", availableTimes);
            model.addAttribute("appointments", availableTimes);
            return new ModelAndView("newAppointment", "vets", vetsFiltered);
        } else {
            appointmentService.saveAppointment(newAppointment);
            redirect.addFlashAttribute("newAppointmentSuccess", true);
            return new ModelAndView("redirect:/appointments");
        }
    }

    private List<Appointment> getFilteredAppointments(Customer customer, List<Appointment> appointments) {
        return appointments.stream()
                .filter(appointment ->
                        appointment.getPet().getCustomer()
                                .getEmail().equals(customer.getEmail()) ||
                                appointment.getVet()
                                        .getEmail().equals(customer.getEmail()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Vet> getFilteredAppointments(List<Vet> vets, Customer customer) {
        return IntStream.range(0, vets.size())
                .filter(i -> customer.getCity().equals(vets.get(i).getCity()))
                .mapToObj(vets::get)
                .collect(Collectors.toList());
    }

    private List<String> getTimes() {
        List<String> times = new ArrayList<>();
        times.add("09.00 - 09.30");
        times.add("09.30 - 10.00");
        times.add("10.00 - 10.30");
        times.add("10.30 - 11.00");
        times.add("11.00 - 11.30");
        times.add("11.30 - 12.00");
        times.add("12.00 - 12.30");
        times.add("13.30 - 14.00");
        times.add("14.00 - 14.30");
        times.add("14.30 - 15.00");
        times.add("15.00 - 15.30");
        times.add("15.30 - 16.00");
        times.add("16.00 - 16.30");
        times.add("16.30 - 17.00");
        times.add("17.00 - 17.30");
        times.add("17.30 - 18.00");

        return times;
    }
}
