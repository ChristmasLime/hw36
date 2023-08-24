package ru.hogwarts.school.testresttemplate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.SchoolApplication;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {
    public static final Student STUD1 = new Student(null, "Roma", 35);
    public static final Student STUD2 = new Student(null, "Anna", 21);

    @Autowired
    TestRestTemplate template;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        template.postForEntity("/student", STUD1, Student.class);
        template.postForEntity("/student", STUD2, Student.class);
    }

    @AfterEach
    void clearDB() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

    }

    private ResponseEntity<Student> createStudent(String name, int age) {
        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        ResponseEntity<Student> response = template.postForEntity("/student", student, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response;
    }


    @Test
    void createStudentTest() {
        ResponseEntity<Student> response = createStudent("Stas", 25);
        Student createdStudent = response.getBody();
        assertThat(createdStudent.getName()).isEqualTo("Stas");
        assertThat(createdStudent.getAge()).isEqualTo(25);
    }

    @Test
    void findStudentTest() {
        ResponseEntity<Student> student = createStudent("Stas", 25);
        Long id = student.getBody().getId();

        ResponseEntity<Student> response = template.getForEntity("/student/" + id, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((response.getBody())).isNotNull();
        assertThat((response.getBody()).getId()).isEqualTo(id);
        assertThat((response.getBody()).getName()).isEqualTo("Stas");
        assertThat((response.getBody()).getAge()).isEqualTo(25);
    }

    @Test
    void editStudentTest() {
        ResponseEntity<Student> response = createStudent("Stas", 25);
        Student student = response.getBody();
        student.setName("Roman");
        template.put("/student", student, Student.class);
        response = template.getForEntity("/student/" + student.getId(), Student.class);
        assertThat(response.getBody().getName()).isEqualTo("Roman");

    }

    @Test
    void deleteStudentTest() {
        ResponseEntity<Student> student = createStudent("Stas", 25);
        Long id = student.getBody().getId();
        template.delete("/student/" + id);
        student = template.getForEntity("/student/" + id, Student.class);
        assertThat(student.getBody().getName()).isNull();
        assertThat(student.getBody().getAge()).isZero();
        assertThat(student.getBody()).isNotNull();
        Optional<Student> deletedStudent = studentRepository.findById(id);
        assertThat(deletedStudent).isEmpty();
    }

    @Test
    void getAllStudentTest() {
        ResponseEntity<Collection> response = template.getForEntity("/student", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Collection<Student> body = response.getBody();
        assertThat(body.isEmpty()).isFalse();
        assertThat(body.size()).isEqualTo(2);
    }

    @Test
    void getStudentByAge() {
        ResponseEntity<Collection> response = template.getForEntity("/student/age/35", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void getStudentsByAgeBetween() {
        ResponseEntity<Student> response = createStudent("Elena", 15);
        Long id = response.getBody().getId();
        ResponseEntity<Collection> ageBetweenResponse = template.getForEntity("/student/age-between?minAge=10&maxAge=20", Collection.class);
        assertThat(ageBetweenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ageBetweenResponse.getBody()).isNotNull();
        assertThat(ageBetweenResponse.getBody().size()).isEqualTo(1);
        template.delete("/student/" + id);
        ageBetweenResponse = template.getForEntity("/student/age-between?minAge=10&maxAge=20", Collection.class);
        assertThat(ageBetweenResponse.getBody()).isEmpty();

    }

    @Test
    void byFaculty() {
        Faculty faculty = new Faculty(null, "Gryffindor", "Red");
        ResponseEntity<Faculty> response = template.postForEntity("/faculty", faculty, Faculty.class);
        Student student = new Student(null, "Harry", 30);
        student.setFaculty(response.getBody());
        ResponseEntity<Student> studentResponse = template.postForEntity("/student", student, Student.class);
        Long faculId = response.getBody().getId();
        ResponseEntity<Collection> students = template.getForEntity("/student/by-faculty?id=" + faculId, Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(students.getBody().isEmpty()).isFalse();
        assertThat(student.getName()).isEqualTo("Harry");
    }

    @Test
    void getCountOfStudentsTest() {
        ResponseEntity<Long> response = template.getForEntity("/student/count", Long.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(2L);

    }

    @Test
    void getAverageAgeOfStudentsTest() {
        ResponseEntity<Double> response = template.getForEntity("/student/average-age", Double.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(28.0);
    }


    @Test
    void findLastFiveStudentsTest() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(null, "Harry", 20));
        students.add(new Student(null, "Ron", 21));
        students.add(new Student(null, "Germiona", 22));
        students.add(new Student(null, "Snape", 23));
        students.add(new Student(null, "Malfoi", 24));
        students.add(new Student(null, "Greg", 25));
        students.add(new Student(null, "Samanta", 26));
        students.add(new Student(null, "Bob", 27));
        for (Student student : students) {
            template.postForEntity("/student", student, Student.class);
        }
        ResponseEntity<Student[]> response = template.getForEntity("/student/last-five", Student[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(5);


        assertThat(response.getBody()[0].getName()).isEqualTo("Bob");
        assertThat(response.getBody()[1].getName()).isEqualTo("Samanta");
        assertThat(response.getBody()[2].getName()).isEqualTo("Greg");
        assertThat(response.getBody()[3].getName()).isEqualTo("Malfoi");
        assertThat(response.getBody()[4].getName()).isEqualTo("Snape");

    }


}

