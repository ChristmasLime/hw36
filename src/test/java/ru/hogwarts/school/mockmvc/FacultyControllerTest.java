package ru.hogwarts.school.mockmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.services.FacultyService;


import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {
    @SpyBean
    FacultyService facultyService;

    @MockBean
    FacultyRepository facultyRepository;
    @MockBean
    StudentRepository studentRepository;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Yellow"));
    }

    @Test
    void findFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");
        when(facultyRepository.findById(1L)).thenReturn(java.util.Optional.of(faculty));

        mockMvc.perform(get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Yellow"));
    }

    @Test
    void editFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        mockMvc.perform(post("/faculty"));

        Faculty editedFaculty = new Faculty(1L, "Slytherin", "Green");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(editedFaculty);
        mockMvc.perform(put("/faculty")
                        .content(objectMapper.writeValueAsString(editedFaculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Slytherin"))
                .andExpect(jsonPath("$.color").value("Green"));
    }
    @Test
    void deleteStudentTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/faculty/" + faculty.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFacul(faculty.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    @Test
    void getAllFacultyTest() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor", "Yellow"),
                new Faculty(2L, "Slytherin", "Green")
        );

        when(facultyService.getAllFacul()).thenReturn(faculties);
        mockMvc.perform(get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Yellow"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"))
                .andExpect(jsonPath("$[1].color").value("Green"));
    }
    @Test
    void getColorFacultyTest() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Slytherin", "Red")
        );

        when(facultyService.getFaculByColor("Red")).thenReturn(faculties);
        mockMvc.perform(get("/faculty/color/Red")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"))
                .andExpect(jsonPath("$[1].color").value("Red"));
    }

    @Test
    void getFacultyNameOrColorIgnoreCaseTest() throws Exception {
        List<Faculty> faculties = Collections.singletonList(
                new Faculty(1L, "Gryffindor", "Yellow")
        );

        when(facultyService.getFacultyNameOrColor("ff")).thenReturn(faculties);
        mockMvc.perform(get("/faculty/search")
                        .param("searchString", "ff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Yellow"));
    }
    @Test
    void byStudent() throws Exception {
        Student student = new Student(1L, "Harry", 22);
        Faculty faculty = new Faculty(1L, "Gryffindor", "Yellow");
        student.setFaculty(faculty);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/by-student/?id=" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Yellow"));

    }


}



