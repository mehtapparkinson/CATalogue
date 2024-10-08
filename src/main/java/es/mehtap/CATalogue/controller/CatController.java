package es.mehtap.CATalogue.controller;


import es.mehtap.CATalogue.model.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    //Updated with stream
    @GetMapping("/not-adopted")
    public List<Cat> getNotAdoptedCats() {
        // Fetch all cats from the database
        List<Cat> allCats = catRepository.findAll();

        // filter the cats that have not been adopted and turn them into List and return them
        List<Cat> notAdoptedCats = allCats.stream()
                .filter(cat -> !cat.isAdopted())
                .toList();

        return notAdoptedCats;
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

    private Cat findCatByIdOrThrow(int id) {
        return catRepository.findById(id).orElseThrow(() -> {
            logger.error("Cat with id {} not found", id);
            return new ResponseStatusException(HttpStatusCode.valueOf(404), "Cat not found");
        });
    }


    //GET SPECIFIC CAT
    @GetMapping("/{id}")
    public Cat getCatById(@PathVariable int id) {
        logger.info("Fetching cat with id {}", id);
        return findCatByIdOrThrow(id);
    }


    //DELETE A CAT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCat(@PathVariable int id) {
        logger.info("Attempting to delete cat with id {}", id);
        try {
            Cat cat = findCatByIdOrThrow(id);
            catRepository.deleteById(id);
            return ResponseEntity.ok("Cat with id " + id + " deleted successfully");
        } catch (ResponseStatusException e) {
            logger.error("Failed to delete cat with id {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cat not found");
        }
    }



    //UPDATE A CAT
    @PutMapping("/{id}")
    public Cat updateCat(@PathVariable int id, @RequestBody Cat updatedCat) {
        logger.info("Updating cat with id {}", id);
        Cat cat = findCatByIdOrThrow(id);

        // Update fields
        cat.setName(updatedCat.getName());
        cat.setBreed(updatedCat.getBreed());
        cat.setAge(updatedCat.getAge());
        cat.setGender(updatedCat.getGender());
        cat.setAdopted(updatedCat.isAdopted());

        return catRepository.save(cat);
    }

    // USE GENERIC METHOD TO UPDATE CAT DATA
    public <T> Cat updateCatField(Cat cat, String fieldType, T newValue) {
        if ("name".equalsIgnoreCase(fieldType)) {
            cat.setName((String) newValue);
        } else if ("age".equalsIgnoreCase(fieldType)) {
            cat.setAge((Integer) newValue);
        } else if ("breed".equalsIgnoreCase(fieldType)) {
            cat.setBreed((String) newValue);
        }
        return catRepository.save(cat);
    }

    //  UPDATE CAT NAME
    @PutMapping("/{id}/update/name")
    public ResponseEntity<Cat> updateCatName(@PathVariable int id, @RequestParam String newName) {
        Cat cat = findCatByIdOrThrow(id);
        Cat updatedCat = updateCatField(cat, "name", newName);
        return ResponseEntity.ok(updatedCat);
    }

    // UPDATE CAT AGE
    @PutMapping("/{id}/update/age")
    public ResponseEntity<Cat> updateCatAge(@PathVariable int id, @RequestParam int newAge) {
        Cat cat = findCatByIdOrThrow(id);
        Cat updatedCat = updateCatField(cat, "age", newAge);
        return ResponseEntity.ok(updatedCat);
    }

    // UPDATE CAT BREED
    @PutMapping("/{id}/update/breed")
    public ResponseEntity<Cat> updateCatBreed(@PathVariable int id, @RequestParam String newBreed) {
        Cat cat = findCatByIdOrThrow(id);
        Cat updatedCat = updateCatField(cat, "breed", newBreed);
        return ResponseEntity.ok(updatedCat);
    }

}
