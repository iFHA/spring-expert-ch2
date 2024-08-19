package dev.fernando.demo.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.fernando.demo.dto.EventDTO;
import dev.fernando.demo.entities.City;
import dev.fernando.demo.entities.Event;
import dev.fernando.demo.repositories.CityRepository;
import dev.fernando.demo.repositories.EventRepository;
import dev.fernando.demo.services.exceptions.DatabaseException;
import dev.fernando.demo.services.exceptions.ResourceNotFoundException;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final CityRepository cityRepository;

    public EventService(
        EventRepository eventRepository,
        CityRepository cityRepository
    ) {
        this.eventRepository = eventRepository;
        this.cityRepository = cityRepository;
    }
    @Transactional(readOnly = true)
    public List<EventDTO> findAll() {
        return this.eventRepository.findAll().stream().map(EventDTO::new).toList();
    }
    @Transactional(readOnly = true)
    public Page<EventDTO> findAllPaged(Pageable pageable) {
        return this.eventRepository.findAll(pageable).map(EventDTO::new);
    }
    protected Event findEntityById(Long id) {
        return this.eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento de Id = %d não encontrado!".formatted(id)));
    }
    @Transactional(readOnly = true)
    public EventDTO findById(Long id) {
        return new EventDTO(this.findEntityById(id));
    }
    @Transactional
    public EventDTO store(EventDTO dto) {
        dto.setId(null);
        City city = cityRepository.findById(dto.getCityId()).orElse(null);
        return new EventDTO(this.eventRepository.save(new Event(dto, city)));
    }
    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event entity = this.findEntityById(id);
        copyDTOToEntity(dto, entity);
        return new EventDTO(this.eventRepository.save(entity));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        this.findEntityById(id);
        try {
            this.eventRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível excluir o evento de id = %d, pois o mesmo possui vínculos!".formatted(id));
        }
    }
    private void copyDTOToEntity(EventDTO dto, Event entity) {
        City city = cityRepository.findById(dto.getCityId()).orElse(null);
        entity.setCity(city);
        entity.setDate(dto.getDate());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
    }
}
