package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

public interface MemoryUser extends JpaRepository<User, Long> {

    Page<User> getUsersByIdIn(List<Long> id, Pageable pageable);

    Boolean existsUserById(Long userId);

}