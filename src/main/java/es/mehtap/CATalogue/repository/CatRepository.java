package es.mehtap.CATalogue.repository;

import es.mehtap.CATalogue.model.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat, Integer> {
    //find not adopted cats method
    List<Cat> findByIsAdopted(boolean isAdopted);

}
