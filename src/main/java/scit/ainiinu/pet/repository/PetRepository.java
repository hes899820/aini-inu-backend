package scit.ainiinu.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {
    // TODO: 필요한 쿼리 메서드를 정의해보세요.
}
