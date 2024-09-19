package es.mehtap.CATalogue.controller;


import es.mehtap.CATalogue.model.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import es.mehtap.CATalogue.repository.CatRepository;

import java.util.List;

@RestController
@RequestMapping("/cats")
public class CatController {
    //INFO
    @Value("${adoption.center.name}")
    private String centerName;

    @Value("${adoption.center.location}")
    private String centerLocation;

    public String getAdoptionCenterInfo() {
        return "Welcome to " + centerName + " located in " + centerLocation;
    }
    //INJECT REPO
    @Autowired
    private CatRepository catRepository;

    //GET ALL NOT ADOPTED CATS
    @GetMapping("/not-adopted")
    public List<Cat> getNotAdoptedCats(){
        return catRepository.findByIsAdopted(false);
    }

    //ADD CAT
    @PostMapping("/add")
    public Cat addCat(@RequestBody Cat cat){
        return catRepository.save(cat);
    }

}
