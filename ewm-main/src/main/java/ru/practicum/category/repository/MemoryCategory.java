package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;

public interface MemoryCategory extends JpaRepository<Category, Long> {

    Boolean existsByNameIgnoreCase(String name);

}