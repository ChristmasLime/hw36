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

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = SchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    public static final Faculty SLYTHERIN = new Faculty(null, "Slytherin", "Green");
    public static final Faculty RAVENCLAW = new Faculty(null, "RavenClaw", "Black");
    @Autowired
    TestRestTemplate template;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        template.postForEntity("/faculty", SLYTHERIN, Faculty.class);
        template.postForEntity("/faculty", RAVENCLAW, Faculty.class);
    }

    @AfterEach
    void clearDB() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private ResponseEntity<Faculty> createFaculty(String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        ResponseEntity<Faculty> response = template.postForEntity("/faculty", faculty, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response;
    }


    @Test
    void createFacultyTest() {

        ResponseEntity<Faculty> faculty = createFaculty("Hufflepuff", "Blue");

        assertThat(faculty.getBody().getName()).isEqualTo("Hufflepuff");
        assertThat(faculty.getBody().getColor()).isEqualTo("Blue");
    }

    @Test
    void findFacultyTest() {
        ResponseEntity<Faculty> faculty = createFaculty("Slytherin", "Green");
        Long id = faculty.getBody().getId();

        ResponseEntity<Faculty> response = template.getForEntity("/faculty/" + id, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Slytherin");
        assertThat(response.getBody().getColor()).isEqualTo("Green");
    }

    @Test
    void editFacultyTest() {
        ResponseEntity<Faculty> response = createFaculty("Hufflepuff", "Blue");

        Faculty faculty = response.getBody();

        faculty.setColor("Green");

        template.put("/faculty", faculty, Faculty.class);
        response = template.getForEntity("/faculty/" + faculty.getId(), Faculty.class);
        assertThat(response.getBody().getColor()).isEqualTo("Green");

    }

    @Test
    void deleteFacultyTest() {
        ResponseEntity<Faculty> faculty = createFaculty("Hufflepuff", "Blue");
        Long id = faculty.getBody().getId();
        template.delete("/faculty/" + id);
        faculty = template.getForEntity("/faculty/" + id, Faculty.class);
        assertThat(faculty.getBody().getColor()).isNull();
        assertThat(faculty.getBody().getName()).isNull();
        assertThat(faculty.getBody()).isNotNull();
        Optional<Faculty> deletedFaculty = facultyRepository.findById(id);
        assertThat(deletedFaculty).isEmpty();
    }


    @Test
    void getAllFacultyTest() {
        ResponseEntity<Collection> response = template.getForEntity("/faculty", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Collection<Faculty> body = response.getBody();
        assertThat(body.isEmpty()).isFalse();
        assertThat(body.size()).isEqualTo(2);

    }

    @Test
    void getFacultyByColorTest() {
        ResponseEntity<Collection> response = template.getForEntity("/faculty/color/Green", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void getFacultyNameOrColorIgnoreCaseTest() {
        ResponseEntity<Collection> searchResponse = template.getForEntity("/faculty/search?searchString=sLytHerin", Collection.class);
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotNull();
        assertThat(searchResponse.getBody().size()).isEqualTo(1);
    }

    @Test
    void byStudent() {
        ResponseEntity<Faculty> response = createFaculty("Gryffindor", "Red");
        Faculty expectedFaculty = response.getBody();
        Student student = new Student();
        student.setFaculty(expectedFaculty);
        ResponseEntity<Student> studentResponse = template.postForEntity("/student", student, Student.class);
        Long studId = studentResponse.getBody().getId();
        response= template.getForEntity("/faculty/by-student?id=" + studId,Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat((response.getBody())).isEqualTo(expectedFaculty);

    }


}
