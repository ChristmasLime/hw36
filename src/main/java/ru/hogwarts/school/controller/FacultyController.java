package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.services.FacultyService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.createFacul(faculty));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> findFaculty(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyService.findFacul(id));
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        if (faculty == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(facultyService.editFacul(faculty));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFacul(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculty() {
        return ResponseEntity.ok(facultyService.getAllFacul());
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getColorFaculty(@PathVariable String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.getFaculByColor(color));
        }
        return ResponseEntity.ok(Collections.emptySet());
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Faculty>> getFacultyNameOrColorIgnoreCase(@RequestParam String searchString) {
        if (searchString!=null&& !searchString.isBlank()) {
            return ResponseEntity.ok(facultyService.getFacultyNameOrColor(searchString));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/by-student")
    public Faculty getStudentsByFacultyId(@RequestParam Long id) {
        return facultyService.getByStudentId(id);
    }
}
