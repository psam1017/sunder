package portfolio.sunder.web.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import portfolio.sunder.web.student.dto.request.StudentSave;
import portfolio.sunder.web.student.dto.response.StudentResponse;
import portfolio.sunder.web.student.service.StudentService;

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
    public StudentResponse find(@PathVariable Long id) {
        return studentService.find(id);
    }
}
