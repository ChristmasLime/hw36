package ru.hogwarts.school.mockmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.services.AvatarService;
import ru.hogwarts.school.services.StudentService;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @SpyBean
    StudentService studentService;
    @MockBean
    StudentRepository studentRepository;
    @MockBean
    FacultyRepository facultyRepository;
    @MockBean
    AvatarService avatarService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createStudentTest() throws Exception {
        Student student = new Student(1L, "Roman", 30);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.age").value("30"));
    }

    @Test
    void findStudentTest() throws Exception {
        Student student = new Student(1L, "Roman", 30);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.age").value("30"));
    }

    @Test
    void editStudentTest() throws Exception {
        Student student = new Student(1L, "Alice", 22);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        mockMvc.perform(post("/student"));

        Student editStudent = new Student(1L, "Bob", 24);
        when(studentRepository.save(any(Student.class))).thenReturn(editStudent);
        mockMvc.perform(put("/student")
                        .content(objectMapper.writeValueAsString(editStudent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.age").value("24"));
    }


    @Test
    void deleteStudentTest() throws Exception {
        Student student = new Student(1L, "Alice", 22);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        mockMvc.perform(post("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/student/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStud(student.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllStudentsTest() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", 22),
                new Student(2L, "Bob", 24)
        );

        when(studentService.getAllStud()).thenReturn(students);

        mockMvc.perform(get("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].age").value("22"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].age").value("24"));
    }

    @Test
    void getAllStudentsByAgeTest() throws Exception {
        int targetAge = 25;
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", 22),
                new Student(2L, "Bob", 25)
        );

        when(studentService.getStudByAge(targetAge)).thenReturn(students);

        mockMvc.perform(get("/student/age/" + targetAge)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].age").value("25"));
    }

    @Test
    void getStudentsByAgeBetweenTest() throws Exception {
        when(studentRepository.findStudByAgeBetween(10, 20)).thenReturn(Arrays.asList(
                new Student(1L, "Roman", 15),
                new Student(2L, "Anna", 19)
        ));
        mockMvc.perform(get("/student/age-between?minAge=10&maxAge=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));


    }


    @Test
    void byFaculty() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry", 22),
                new Student(2L, "Ron", 23)
        );
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");
        faculty.setStudents(students);

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(get("/student/by-faculty?id=" + faculty.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Harry"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Ron"));


    }

    @Test
    void getCountOfStudentsTest() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", 22),
                new Student(2L, "Bob", 24),
                new Student(3L, "Charlie", 20),
                new Student(4L, "David", 21),
                new Student(5L, "Eve", 23)
        );

        when(studentService.getCountOfStudents()).thenReturn((long) students.size());
        mockMvc.perform(get("/student/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(students.size()));
    }

    @Test
    void getAverageAgeOfStudentsTest() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", 22),
                new Student(2L, "Bob", 24),
                new Student(3L, "Charlie", 20)
        );

        Double averageAge = students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);

        when(studentService.getAverageAgeOfStudents()).thenReturn(averageAge);
        mockMvc.perform(get("/student/average-age")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(averageAge));
    }
    @Test
    void findLastFiveStudentsTest() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Alice", 22),
                new Student(2L, "Bob", 24),
                new Student(3L, "Charlie", 20),
                new Student(4L, "David", 21),
                new Student(5L, "Eve", 23),
                new Student(6L, "Frank", 25),
                new Student(7L, "Grace", 19),
                new Student(8L, "Helen", 27),
                new Student(9L, "Ivan", 26),
                new Student(10L, "John", 18)
        );
        Pageable pageable = PageRequest.of(0, 5);
        Page<Student> studentPage = new PageImpl<>(students.subList(5, 10), pageable, students.size());

        when(studentRepository.findLastFiveStudents(any())).thenReturn(studentPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/last-five")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id").value(6))
                .andExpect(jsonPath("$[1].id").value(7))
                .andExpect(jsonPath("$[2].id").value(8))
                .andExpect(jsonPath("$[3].id").value(9))
                .andExpect(jsonPath("$[4].id").value(10));
    }

}



