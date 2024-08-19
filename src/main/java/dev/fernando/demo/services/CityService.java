package dev.fernando.demo.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.fernando.demo.dto.CityDTO;
import dev.fernando.demo.entities.City;
import dev.fernando.demo.repositories.CityRepository;
import dev.fernando.demo.services.exceptions.DatabaseException;
import dev.fernando.demo.services.exceptions.ResourceNotFoundException;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(final CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {
        return this.cityRepository.findAllByOrderByName().stream().map(CityDTO::new).toList();
    }
    @Transactional(readOnly = true)
    public Page<CityDTO> findAllPaged(Pageable pageable) {
        return this.cityRepository.findAll(pageable).map(CityDTO::new);
    }
    protected City findEntityById(Long id) {
        return this.cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria de Id = %d não encontrada!".formatted(id)));
    }
    @Transactional(readOnly = true)
    public CityDTO findById(Long id) {
        return new CityDTO(this.findEntityById(id));
    }
    @Transactional
    public CityDTO store(CityDTO dto) {
        dto.setId(null);
        return new CityDTO(this.cityRepository.save(new City(dto)));
    }
    @Transactional
    public CityDTO update(Long id, CityDTO dto) {
        City entity = this.findEntityById(id);
        entity.setName(dto.getName());
        return new CityDTO(this.cityRepository.save(entity));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        City entity = this.findEntityById(id);
        try {
            this.cityRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível excluir a cidade de id = %d, pois a mesma possui vínculos!".formatted(id));
        }
    }
}
