package es.mehtap.CATalogue.controller;


import es.mehtap.CATalogue.model.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Cat> addCat(@RequestBody Cat cat) {
        // Validate name
        if (cat.getName() == null || cat.getName().isEmpty()) {
            logger.warn("Attempted to add a cat with an empty name");
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Cat name is required");
        }

        // Validate breed
        if (cat.getBreed() == null || cat.getBreed().isEmpty()) {
            logger.warn("Attempted to add a cat with an empty breed");
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Cat breed is required");
        }

        // Validate age (for example, age must be non-negative)
        if (cat.getAge() < 0) {
            logger.warn("Attempted to add a cat with a negative age");
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Cat age cannot be negative");
        }

        // Validate gender
        if (cat.getGender() == null || cat.getGender().isEmpty()) {
            logger.warn("Attempted to add a cat with an empty gender");
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Cat gender is required");
        }

        logger.info("Adding a new cat: {}", cat.getName());
        Cat savedCat = catRepository.save(cat);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(savedCat);
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

    //UPDATE A CAT
    @PutMapping("/{id}")
    public Cat updateCat(@PathVariable int id, @RequestBody Cat updatedCat) {
        if (catRepository.existsById(id)) {
            logger.info("Updating cat with id {}", id);
            Cat cat = catRepository.findById(id).get();
            cat.setName(updatedCat.getName());
            cat.setBreed(updatedCat.getBreed());
            cat.setAge(updatedCat.getAge());
            cat.setGender(updatedCat.getGender());
            cat.setAdopted(updatedCat.isAdopted());
            return catRepository.save(cat);
        } else {
            logger.error("Cat with id {} not found", id);
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Cat not found");
        }
    }

}
