package psam.portfolio.sunder.english.web.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.web.student.dto.request.StudentSave;
import psam.portfolio.sunder.english.web.student.dto.response.StudentResponse;
import psam.portfolio.sunder.english.web.student.service.StudentService;

@RequiredArgsConstructor
@RequestMapping("/api/student")
@RestController
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public long register(@RequestBody StudentSave student) {
        return studentService.register(student);
    }

    @GetMapping("/{id}")
    public StudentResponse find(@PathVariable("id") Long id) {
        return studentService.find(id);
    }
}
