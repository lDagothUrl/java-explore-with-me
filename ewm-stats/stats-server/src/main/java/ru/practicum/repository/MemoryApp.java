package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.App;

import java.util.Optional;

public interface MemoryApp extends JpaRepository<App, Long> {

    Optional<App> findByName(String name);

}