package ru.hogwarts.school.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class StudentService {


    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;


    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student createStud(Student student) {
        return studentRepository.save(student);
    }

    public Student findStud(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public Student editStud(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStud(Long id) {
        studentRepository.deleteById(id);

    }

    public Collection<Student> getAllStud() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudByAge(int age) {
        return studentRepository.getStudByAge(age);
    }

    public Collection<Student> getStudentsAgeBetween(int minAge, int maxAge) {
        return studentRepository.findStudByAgeBetween(minAge, maxAge);
    }


    public Collection<Student> getByFacultyId(Long id) {
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .orElseThrow(NoSuchElementException::new);
    }

    public Long getCountOfStudents() {
        return studentRepository.getCountOfStudents();
    }

    public Double getAverageAgeOfStudents() {
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<Student> findLastFiveStudents() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Student> studentsPage = studentRepository.findLastFiveStudents(pageRequest);
        return studentsPage.getContent();
    }
}

