package pw_be.Service;

import pw_be.Exceptions.ResourceNotFoundException;
import pw_be.Mapper.Fornitori_Mapper;
import org.example.pw_be.model.dto.FornitoreDto;
import org.example.pw_be.model.dto.FornitoreRequestDto;
import pw_be.Model.Fornitore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw_be.Repository.Fornitori_Repository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class Fornitore_Service {

    private final Fornitori_Repository fornitoreRepository;
    private final Fornitori_Mapper fornitoreMapper;

    public Fornitore_Service(Fornitori_Repository fornitoreRepository,
                             Fornitori_Mapper fornitoreMapper) {
        this.fornitoreRepository = fornitoreRepository;
        this.fornitoreMapper = fornitoreMapper;
    }

    public FornitoreDto createFornitore(FornitoreRequestDto requestDto) {
        Fornitore fornitore = fornitoreMapper.toEntity(requestDto);
        Fornitore savedFornitore = fornitoreRepository.save(fornitore);
        return fornitoreMapper.toDto(savedFornitore);
    }

    public FornitoreDto getFornitoreById(Long id) {
        Fornitore fornitore = fornitoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornitore non trovato con id: " + id));
        return fornitoreMapper.toDto(fornitore);
    }

    public List<FornitoreDto> getAllFornitori() {
        List<Fornitore> fornitori = fornitoreRepository.findAll();
        return fornitori.stream()
                .map(fornitoreMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FornitoreDto> searchFornitori(String query) {
        // Implementa la ricerca in base ai tuoi requisiti
        List<Fornitore> fornitori = fornitoreRepository.findByNomeContainingIgnoreCase(query);
        return fornitori.stream()
                .map(fornitoreMapper::toDto)
                .collect(Collectors.toList());
    }

    public FornitoreDto updateFornitore(Long id, FornitoreRequestDto requestDto) {
        Fornitore existingFornitore = fornitoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornitore non trovato con id: " + id));

        fornitoreMapper.updateEntityFromDto(requestDto, existingFornitore);
        Fornitore updatedFornitore = fornitoreRepository.save(existingFornitore);
        return fornitoreMapper.toDto(updatedFornitore);
    }

    public void deleteFornitore(Long id) {
        Fornitore existingFornitore = fornitoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornitore non trovato con id: " + id));
        fornitoreRepository.delete(existingFornitore);
    }
}
