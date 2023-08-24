package ru.hogwarts.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> getStudByAge(int age);

    Collection<Student> findStudByAgeBetween(int min, int max);

    @Query("SELECT COUNT(s) FROM Student s")
    Long getCountOfStudents();

    @Query("SELECT AVG(s.age) FROM Student s")
    Double getAverageAgeOfStudents();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    Page<Student> findLastFiveStudents(Pageable pageable);
}
