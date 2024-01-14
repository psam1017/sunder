package portfolio.sunder.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import portfolio.sunder.domain.student.dto.request.StudentSave;
import portfolio.sunder.domain.student.service.StudentService;
import portfolio.sunder.domain.user.enumeration.UserRole;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class Controller {

    private final JwtUtils jwtUtils;
    private final StudentService studentService;

    @PostMapping("/new")
    public long newStudent(@RequestBody StudentSave studentSave) {
        return studentService.register(studentSave);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/token/{id}")
    public String buildToken(@PathVariable("id") Long id) {
        return jwtUtils.generateToken(id, 1000 * 60 * 60);
    }
}
