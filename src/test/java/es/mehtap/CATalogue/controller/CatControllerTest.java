package es.mehtap.CATalogue.controller;

import es.mehtap.CATalogue.model.Cat;
import es.mehtap.CATalogue.repository.CatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatControllerTest {
    // Mock the repository
    @Mock
    private CatRepository catRepository;

    // Inject the mock into the controller
    @InjectMocks
    private CatController catController;

    @Test
    void when_getNotAdoptedCats_then_returnOnlyNotAdoptedCats() {
        //ARRANGE
        Cat cat1 = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        Cat cat2 = new Cat(2, "Mittens", "Persian", 4, "Female", true);
        Cat cat3 = new Cat(3, "Shadow", "Tabby", 1, "Female", false);

        List<Cat> allCats = Arrays.asList(cat1, cat2, cat3);

        // Mock repository behavior
        when(catRepository.findAll()).thenReturn(allCats);

        // ACT
        List<Cat> notAdoptedCats = catController.getNotAdoptedCats();

        // ASSERT
        assertEquals(2, notAdoptedCats.size());
        assertTrue(notAdoptedCats.contains(cat1));
        assertTrue(notAdoptedCats.contains(cat3));
        assertFalse(notAdoptedCats.contains(cat2));

        //STREAM TO VERIFY
        boolean containsWhiskers = notAdoptedCats.stream().anyMatch(cat -> cat.getId() == 1);
        boolean containsShadow = notAdoptedCats.stream().anyMatch(cat -> cat.getId() == 3);

        assertTrue(containsWhiskers && containsShadow, "Whiskers and Shadow should be in the not adopted list");

    }

    @Test
    void when_addValidCat_then_catIsSaved() {
        // ARRANGE
        Cat validCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);

        // Mock repository behavior
        when(catRepository.save(validCat)).thenReturn(validCat);

        // ACT
        ResponseEntity<Cat> response = catController.addCat(validCat);

        //ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(validCat, response.getBody());
    }

    @Test
    void when_addCatWithEmptyName_then_throwsBadRequest() {
        // Arrange
        Cat invalidCat = new Cat(1, "", "Siamese", 2, "Male", false);

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.addCat(invalidCat)
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cat name is required", exception.getReason());
    }

    @Test
    void when_deleteCatById_then_catIsDeleted() {
        // Arrange
        Cat existingCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        when(catRepository.findById(1)).thenReturn(Optional.of(existingCat));

        // Act
        ResponseEntity<String> response = catController.deleteCat(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cat with id 1 deleted successfully", response.getBody());

    }

    @Test
    void when_deleteNonExistentCat_then_throwsNotFoundException() {
        // Arrange
        when(catRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.deleteCat(999)
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cat not found", exception.getReason());
    }

    @Test
    void when_addCatWithEmptyBreed_then_throwsBadRequest() {
        // Arrange
        Cat invalidCat = new Cat(1, "Whiskers", "", 2, "Male", false);

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.addCat(invalidCat)
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cat breed is required", exception.getReason());
    }

    @Test
    void when_addCatWithNegativeAge_then_throwsBadRequest() {
        // Arrange
        Cat invalidCat = new Cat(1, "Whiskers", "Siamese", -1, "Male", false);

        try {
            // Act
            catController.addCat(invalidCat);

        } catch (ResponseStatusException exception) {
            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Cat age cannot be negative", exception.getReason());
        }
    }

    @Test
    void when_addCatWithEmptyGender_then_throwsBadRequest() {
        // Arrange
        Cat invalidCat = new Cat(1, "Whiskers", "Siamese", 2, "", false);

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.addCat(invalidCat)
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cat gender is required", exception.getReason());
    }

    @Test
    void when_addCatWithNullGender_then_throwsBadRequest() {
        // Arrange
        Cat invalidCat = new Cat(1, "Whiskers", "Siamese", 2, null, false);

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.addCat(invalidCat)
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cat gender is required", exception.getReason());
    }

    @Test
    void when_getCatById_then_return () {
        //ARRANGE
        Cat validCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);

        when(catRepository.findById(1)).thenReturn(Optional.of(validCat));
        //ACT
        Cat cat = catController.getCatById(1);
        //ASSERT
        assertEquals(validCat, cat);
    }

    @Test
    void when_getCatById_then_throwNotFound () {
        //ARRANGE
        when(catRepository.findById(999)).thenReturn(Optional.empty());
        //ACT
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.getCatById(999)
        );
        //ASSERT
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cat not found", exception.getReason());
    }

    @Test
    void when_updateCat_then_returnUpdatedCat () {
        //ARRANGE
        Cat validCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        Cat updatedCat = new Cat(1, "Shadow", "Persian", 4, "Male", true);

        when(catRepository.findById(1)).thenReturn(Optional.of(validCat));
        when(catRepository.save(updatedCat)).thenReturn(updatedCat);

        //ACT
        Cat cat = catController.updateCat(1, updatedCat);
        //ASSERT
        assertEquals(updatedCat, cat);
        }

    @Test
    void when_updateNonExistentCat_then_throwNotFound () {
        //ARRANGE
        Cat updatedCat = new Cat(999, "Shadow", "Persian", 4, "Male", true);
        when(catRepository.findById(999)).thenReturn(Optional.empty());
        //ACT
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> catController.updateCat(999, updatedCat)
        );
        //ASSERT
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cat not found", exception.getReason());
    }

    @Test
    void when_updateCatName_then_nameIsUpdated() {
        // Arrange change name of cat
        Cat existingCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        Cat updatedCat = new Cat(1, "Fluffy", "Siamese", 2, "Male", false);

        when(catRepository.findById(1)).thenReturn(Optional.of(existingCat));
        when(catRepository.save(existingCat)).thenReturn(updatedCat);

        // Act
        ResponseEntity<Cat> response = catController.updateCatName(1, "Fluffy");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fluffy", response.getBody().getName());
    }

    @Test
    void when_updateCatAge_then_ageIsUpdated() {
        // Arrange change age of cat
        Cat existingCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        Cat updatedCat = new Cat(1, "Whiskers", "Siamese", 4, "Male", false);

        when(catRepository.findById(1)).thenReturn(Optional.of(existingCat));
        when(catRepository.save(existingCat)).thenReturn(updatedCat);

        // Act
        ResponseEntity<Cat> response = catController.updateCatAge(1, 4);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().getAge());
    }

    @Test
    void when_updateCatBreed_then_breedIsUpdated() {
        // Arrange change breed of cat
        Cat existingCat = new Cat(1, "Whiskers", "Siamese", 2, "Male", false);
        Cat updatedCat = new Cat(1, "Whiskers", "Persian", 2, "Male", false);
        
        when(catRepository.findById(1)).thenReturn(Optional.of(existingCat));
        when(catRepository.save(existingCat)).thenReturn(updatedCat);

        // Act
        ResponseEntity<Cat> response = catController.updateCatBreed(1, "Persian");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Persian", response.getBody().getBreed());
    }



}