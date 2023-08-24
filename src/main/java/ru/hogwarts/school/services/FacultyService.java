package ru.hogwarts.school.services;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFacul(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFacul(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public Faculty editFacul(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void deleteFacul(Long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFacul() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFaculByColor(String color) {
        return facultyRepository.getFacultiesByColor(color);
    }

    public Collection<Faculty> getFacultyNameOrColor(String searchString) {
        return facultyRepository.getFacultyByNameIgnoreCaseOrColorIgnoreCase(searchString, searchString);
    }

    public Faculty getByStudentId(Long id) {
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .orElseThrow(NoSuchElementException::new);


    }

}
