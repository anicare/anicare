package com.anicare.project.controller;

import com.anicare.project.model.Appointment;
import com.anicare.project.model.City;
import com.anicare.project.model.Customer;
import com.anicare.project.model.Pet;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static boolean isEdit = false;
    private static String editTime = "";

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

    @RequestMapping(value = "/editAppointment", method = RequestMethod.GET)
    public ModelAndView editAppointment(Long id, Model model, Principal principal) {
        Appointment appointment = appointmentService.getOne(id);

        Customer vet = appointment.getVet();
        Pet pet = appointment.getPet();

        List<Customer> vets = vetService.allVets();
        List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));

        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("vet", vet);
        model.addAttribute("pet", pet);
        model.addAttribute("times", appointment.getToTime());

        isEdit = true;
        editTime = appointment.getToTime();

        return new ModelAndView("editAppointment", "appointment", appointment);
    }

    @RequestMapping(value = "/rateAppointment", method = RequestMethod.GET)
    public ModelAndView rateAppointment(Long id, Model model) {
        Appointment appointment = appointmentService.getOne(id);

        List<String> ratings = Arrays.asList("★", "★★", "★★★", "★★★★", "★★★★★");
        model.addAttribute("rates", ratings);

        return new ModelAndView("rateAppointment", "appointment", appointment);
    }

    @RequestMapping(value = "/saveRateAppointment", method = RequestMethod.POST)
    public ModelAndView saveRateAppointment(Long id, String rating, Model model, Principal principal) {
        switch (rating) {
            case "★":
                rating = "1";
                break;
            case "★★":
                rating = "2";
                break;
            case "★★★":
                rating = "3";
                break;
            case "★★★★":
                rating = "4";
                break;
            default:
                rating = "5";
                break;
        }
        Appointment appointment = appointmentService.getOne(id);
        appointment.setRating(rating);
        appointmentService.saveAppointment(appointment);

        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("customer", customer);

        List<Appointment> appointments = appointmentService.allAppointments();
        List<Appointment> appointmentsByCustomer = getFilteredAppointments(customer, appointments);

        return new ModelAndView("appointments", "appointments", appointmentsByCustomer);
    }

    @RequestMapping(value = "/deleteAppointment", method = RequestMethod.POST)
    public ModelAndView deleteAppointment(Long id, Model model, Principal principal) {
        Appointment appointmentFound = appointmentService.getOne(id);

        appointmentService.remove(appointmentFound);

        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("customer", customer);

        List<Appointment> appointments = appointmentService.allAppointments();
        List<Appointment> appointmentsByCustomer = getFilteredAppointments(customer, appointments);

        return new ModelAndView("appointments", "appointments", appointmentsByCustomer);
    }

    @RequestMapping(value = "/vets", method = RequestMethod.GET)
    public ModelAndView vets() {
        List<Customer> vets = vetService.allVets();
        List<Appointment> appointments = appointmentService.allAppointments();
        getRatings(vets, appointments);
        return new ModelAndView("vets", "vets", vets);
    }

    @RequestMapping(value = "/newAppointment", method = RequestMethod.GET)
    public ModelAndView newAppointment(Model model, Principal principal) {
        model.addAttribute("appointment", new Appointment());

        List<Customer> vets = vetService.allVets();

        List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
        model.addAttribute("pets", pets);

        List<City> cities = cityService.allCities();
        model.addAttribute("cities", cities);
        model.addAttribute("times", getTimes());

        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("customer", customer);

        List<Customer> vetsFiltered = getFilteredAppointmentsByBreak(vets, customer, true);
        List<Customer> vetsOnBreak = getFilteredAppointmentsByBreak(vets, customer, false);

        isEdit = false;
        editTime = "";

        handleBreaks(model, vetsOnBreak);
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
        Customer vet = newAppointment.getVet();

        model.addAttribute("notAvailable", false);
        model.addAttribute("timeRequired", false);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(toDate);

        boolean exists = false;
        List<String> availableTimes = getTimes();
        List<Appointment> appointments = appointmentService.allAppointments();

        for (Appointment appointment : appointments) {
            if (appointment.getToDate().toString().equals(formattedDate) && appointment.getVet().equals(vet)) {
                if (appointment.getToTime().equals(toTime) && (!isEdit || !toTime.equals(editTime))) {
                    availableTimes.remove(appointment.getToTime());
                    exists = true;
                }
            }
        }

        if (toTime.equals("0")) {
            String timeRequiredString = "Time is required.";

            List<Customer> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);
            List<Customer> vetsFiltered = getFilteredAppointmentsByBreak(vets, customer, true);

            model.addAttribute("timeRequired", true);
            model.addAttribute("timeRequiredString", timeRequiredString);
            model.addAttribute("times", availableTimes);
            return new ModelAndView("newAppointment", "vets", vetsFiltered);
        }

        if (exists) {
            String availableTimesString = "";

            List<Customer> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);

            List<Customer> vetsFiltered = getFilteredAppointmentsByBreak(vets, customer, true);
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
            List<Customer> vets = vetService.allVets();

            List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
            model.addAttribute("pets", pets);

            List<City> cities = cityService.allCities();
            model.addAttribute("cities", cities);

            Customer customer = customerService.findByUsername(principal.getName());
            model.addAttribute("customer", customer);

            List<Customer> vetsFiltered = getFilteredAppointmentsByBreak(vets, customer, true);
            model.addAttribute("times", availableTimes);
            model.addAttribute("appointments", availableTimes);
            return new ModelAndView("newAppointment", "vets", vetsFiltered);
        } else {
            newAppointment.setRating("-");
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

    private List<Customer> getFilteredAppointmentsByBreak(List<Customer> vets, Customer customer, boolean isEnabled) {
        return IntStream.range(0, vets.size())
                .filter(i -> ((customer.getCity().equals(vets.get(i).getCity())) && vets.get(i).isEnabled() == isEnabled))
                .mapToObj(vets::get)
                .collect(Collectors.toList());
    }

    private void handleBreaks(Model model, List<Customer> vetsOnBreak) {
        String vetsOnBreakMessage = "";
        model.addAttribute("notAvailable", false);

        if (vetsOnBreak.size() > 1) {
            vetsOnBreakMessage += "The vets ";
            for (int i = 0; i < vetsOnBreak.size(); i++) {
                vetsOnBreakMessage += vetsOnBreak.get(i).getFirstName() + " " + vetsOnBreak.get(i).getLastName();
                if (!(i == vetsOnBreak.size() - 1)) {
                    vetsOnBreakMessage += ", ";
                }
            }

            vetsOnBreakMessage += " are in vacation.";

            model.addAttribute("notAvailable", true);
            model.addAttribute("timeList", vetsOnBreakMessage);
        } else if (vetsOnBreak.size() == 1) {
            vetsOnBreakMessage += "The vet " + vetsOnBreak.get(0).getFirstName() + " " + vetsOnBreak.get(0).getLastName() + " is in vacation.";

            model.addAttribute("notAvailable", true);
            model.addAttribute("timeList", vetsOnBreakMessage);
        }
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

    private void getRatings(List<Customer> vets, List<Appointment> appointments) {
        int i = 0;
        while (i < vets.size()) {
            double total = 0;
            double count = 0;
            List<Appointment> appointmentByVet = getFilteredAppointments(vets.get(i), appointments);
            for (Appointment appointment : appointmentByVet) {
                if (!appointment.getRating().equals("-")) {
                    total += Integer.parseInt(appointment.getRating());
                    count++;
                }
            }
            if (!Double.isNaN(total / count)) {
                DecimalFormat df = new DecimalFormat("#.0");
                Customer vet = customerService.findByUsername(vets.get(i).getUsername());
                vet.setRating(String.valueOf(df.format(total / count)));
                customerService.updateCustomer(vet);
            }
            i++;
        }
    }
}
