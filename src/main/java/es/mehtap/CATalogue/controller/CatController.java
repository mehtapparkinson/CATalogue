package es.mehtap.CATalogue.controller;


import es.mehtap.CATalogue.model.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import es.mehtap.CATalogue.repository.CatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/cats")
public class CatController {

    //CREATE LOGGER
    private static final Logger logger = LoggerFactory.getLogger(CatController.class);

    //INFO
    @Value("${adoption.center.name}")
    private String centerName;

    @Value("${adoption.center.location}")
    private String centerLocation;

    public String getAdoptionCenterInfo() {
        logger.info("Retrieving adoption center information for: {}", centerName);
        return "Welcome to " + centerName + " located in " + centerLocation;
    }
    //INJECT REPO
    @Autowired
    private CatRepository catRepository;

    //GET ALL NOT ADOPTED CATS
    @GetMapping("/not-adopted")
    public List<Cat> getNotAdoptedCats(){
        logger.info("Fetching all not-adopted cats");
        return catRepository.findByIsAdopted(false);
    }

    //ADD CAT
    @PostMapping("/add")
    public Cat addCat(@RequestBody Cat cat){
        if (cat.getName() == null || cat.getName().isEmpty()) {
            logger.warn("Attempted to add a cat with an empty name");
        }
        logger.info("Adding a new cat: {}", cat.getName());
        return catRepository.save(cat);
    }

    @GetMapping("/{id}")
    public Cat getCatById(@PathVariable int id) {
        return catRepository.findById(id).orElseThrow(() -> {
            logger.error("Cat with id {} not found", id);
            return new RuntimeException("Cat not found");
        });
    }


}
