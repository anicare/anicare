package com.anicare.project.controller;

import com.anicare.project.model.Appointment;
import com.anicare.project.model.Pet;
import com.anicare.project.service.CustomerService;
import com.anicare.project.service.PetService;
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
import java.util.List;

@Controller
public class PetController {

    private final CustomerService customerService;
    private final PetService petService;

    private long id;

    @Autowired
    public PetController(CustomerService customerService, PetService petService) {
        this.customerService = customerService;
        this.petService = petService;
    }

    @RequestMapping(value = "/pets", method = RequestMethod.GET)
    public ModelAndView users(Principal principal) {
        List<Pet> pets = petService.petsByCustomer(customerService.findByUsername(principal.getName()));
        return new ModelAndView("pets", "pets", pets);
    }

    @RequestMapping(value = "/newPet", method = RequestMethod.GET)
    public ModelAndView newPet() {
        Pet pet = new Pet();
        pet.setNote("");
        return new ModelAndView("newPet", "pet", pet);
    }

    @RequestMapping(value = "/saveNewPet", method = RequestMethod.POST)
    public ModelAndView saveNewPet(@Valid Pet pet,
                                   BindingResult result,
                                   Principal principal,
                                   final RedirectAttributes redirect) {
        long millis = 0;
        long current = 0;

        if (pet.getDateOfBirth() != null) {
            millis = pet.getDateOfBirth().getTime();
            current = System.currentTimeMillis();
        }

        if (current < millis) {
            redirect.addFlashAttribute("past", true);
            return new ModelAndView("redirect:/newPet");
        }

        if (!(result.hasErrors())) {
            pet.setCustomer(customerService.findByUsername(principal.getName()));
            pet.setName(refactor(pet.getName()));
            petService.savePet(pet);

            redirect.addFlashAttribute("newPetSuccess", true);
            return new ModelAndView("redirect:/pets");
        } else return new ModelAndView("newPet");
    }

    @RequestMapping(value = "/updatePet", method = RequestMethod.POST)
    public ModelAndView updateCustomer(@Valid Pet pet) {
        Pet petResults = getResults(pet);
        petService.savePet(petResults);
        return new ModelAndView("redirect:/petInfo?id=" + id, "updatedPet", true);
    }

    @RequestMapping(value = "/petInfo", method = RequestMethod.GET)
    public ModelAndView petInfo(long id, Model model) {
        Pet pet = petService.getOne(id);
        model.addAttribute("pet", pet);
        this.id = id;

        List<Appointment> appointments = pet.getAppointment();
        model.addAttribute(appointments);
        model.addAttribute("hasPhoto", pet.getImageUrl() != null && !pet.getImageUrl().equals(""));
        return new ModelAndView("petinfo", "pet", pet);
    }


    private String refactor(String note) {
        String noteNew = note.replaceAll("ğ", "g");
        noteNew = noteNew.replaceAll("ç", "c");
        noteNew = noteNew.replaceAll("ş", "s");
        noteNew = noteNew.replaceAll("ü", "u");
        noteNew = noteNew.replaceAll("ö", "o");
        noteNew = noteNew.replaceAll("ı", "i");
        noteNew = noteNew.replaceAll("Ğ", "G");
        noteNew = noteNew.replaceAll("Ç", "C");
        noteNew = noteNew.replaceAll("Ş", "S");
        noteNew = noteNew.replaceAll("Ü", "U");
        noteNew = noteNew.replaceAll("Ö", "O");
        noteNew = noteNew.replaceAll("İ", "I");

        return noteNew;
    }

    private Pet getResults(Pet pet) {

        Pet petUpdated = petService.getOne(id);

        if (petUpdated.getNote() == null)
            petUpdated.setNote("");
        if (pet.getNote() != null)
            petUpdated.setNote(pet.getNote());
        if (petUpdated.getVaccine() == null)
            petUpdated.setVaccine("");
        if (pet.getVaccine() != null)
            petUpdated.setVaccine(pet.getVaccine());
        if (petUpdated.getLabResults() == null)
            petUpdated.setLabResults("");
        if (pet.getLabResults() != null)
            petUpdated.setLabResults(pet.getLabResults());

        petUpdated.setNote(refactor(petUpdated.getNote()));
        petUpdated.setVaccine(refactor(petUpdated.getVaccine()));
        petUpdated.setLabResults(refactor(petUpdated.getLabResults()));

        return petUpdated;
    }
}
