package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty,Long> {
    Collection<Faculty> getFacultiesByColor(String color);

    Collection<Faculty> getFacultyByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

}