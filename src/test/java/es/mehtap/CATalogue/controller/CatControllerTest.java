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



}