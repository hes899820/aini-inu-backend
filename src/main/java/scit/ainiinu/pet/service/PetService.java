package scit.ainiinu.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.pet.dto.response.BreedResponse;
import scit.ainiinu.pet.dto.response.PersonalityResponse;
import scit.ainiinu.pet.repository.BreedRepository;
import scit.ainiinu.pet.repository.PersonalityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final BreedRepository breedRepository;
    private final PersonalityRepository personalityRepository;

    /**
     * 전체 견종 목록 조회
     */
    public List<BreedResponse> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(BreedResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 성격 목록 조회
     */
    public List<PersonalityResponse> getAllPersonalities() {
        return personalityRepository.findAll().stream()
                .map(PersonalityResponse::from)
                .collect(Collectors.toList());
    }
}