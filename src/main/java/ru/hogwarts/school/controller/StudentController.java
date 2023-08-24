package ru.hogwarts.school.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.services.AvatarService;
import ru.hogwarts.school.services.StudentService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {


    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStud(student));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudent(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.findStud(id));
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        if (student == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(studentService.editStud(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        studentService.deleteStud(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStud());
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<Student>> getAllStudentsByAge(@PathVariable int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.getStudByAge(age));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/age-between")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(@RequestParam int minAge,
                                                                       @RequestParam int maxAge) {
        if (minAge > maxAge) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(studentService.getStudentsAgeBetween(minAge, maxAge));
    }


    @GetMapping("/by-faculty")
    public Collection<Student> getAllByFaculty(@RequestParam Long id) {
        return studentService.getByFacultyId(id);
    }

    @PostMapping(value = "/{studentId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> save(@PathVariable Long studentId, @RequestBody MultipartFile multipartFile) {
        try {
            return ResponseEntity.ok(avatarService.save(studentId, multipartFile));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCountOfStudents() {
        Long count = studentService.getCountOfStudents();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAgeOfStudents() {
        Double averageAge = studentService.getAverageAgeOfStudents();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> findLastFiveStudents() {
        List<Student> students = studentService.findLastFiveStudents();
        return ResponseEntity.ok(students);
    }


}






