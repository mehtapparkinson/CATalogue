package es.mehtap.CATalogue.controller;


import es.mehtap.CATalogue.model.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import es.mehtap.CATalogue.repository.CatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

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

    //GET ALL CATS
    @GetMapping("/")
    public List<Cat> getAllCats(){
        logger.info("Fetching all cats");
        return catRepository.findAll();
    }

    //GET ALL NOT ADOPTED CATS
    @GetMapping("/not-adopted")
    public List<Cat> getNotAdoptedCats(){
        logger.info("Fetching all not-adopted cats");
        return catRepository.findByIsAdopted(false);
    }

    //GET ALL ADOPTED CATS
    @GetMapping("/adopted")
    public List<Cat> getAdoptedCats(){
        if (catRepository.findByIsAdopted(true).isEmpty()) {
            logger.warn("No cats have been adopted yet");
        }
        logger.info("Fetching all adopted cats");
        return catRepository.findByIsAdopted(true);
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

    //GET SPECIFIC CAT
    @GetMapping("/{id}")
    public Cat getCatById(@PathVariable int id) {
        logger.info("Fetching cat with id {}", id);
        if (catRepository.existsById(id)) {
            return catRepository.findById(id).get();
        } else {
            logger.error("Cat with id {} not found", id);
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Cat not found");
        }
    }

    //DELETE A CAT
    @DeleteMapping("/{id}")
    public void deleteCat(@PathVariable int id) {
        if (catRepository.existsById(id)) {
            logger.info("Deleting cat with id {}", id);
            catRepository.deleteById(id);
        } else {
            logger.error("Cat with id {} not found", id);
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Cat not found");
        }
    }


}
