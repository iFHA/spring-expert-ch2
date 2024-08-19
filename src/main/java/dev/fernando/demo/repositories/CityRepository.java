package dev.fernando.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.fernando.demo.entities.City;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAllByOrderByName();
}
