package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.Location;

public interface MemoryLocation extends JpaRepository<Location, Long> {

    Location findLocationByLatAndLon(Float lat, Float lon);

}