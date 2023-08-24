package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;


import java.util.List;
import java.util.Optional;


public interface AvatarRepository extends JpaRepository<Avatar,Long> {

    Optional<Avatar> findFirstByStudent(Student student);

    List<Avatar> findAll();
}
