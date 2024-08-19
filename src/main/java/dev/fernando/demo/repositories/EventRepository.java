package dev.fernando.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.fernando.demo.entities.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
