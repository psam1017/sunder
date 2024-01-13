package portfolio.sunder.domain.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import portfolio.sunder.domain.student.dto.request.StudentSave;
import portfolio.sunder.domain.student.dto.response.StudentResponse;
import portfolio.sunder.domain.student.service.StudentService;

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
