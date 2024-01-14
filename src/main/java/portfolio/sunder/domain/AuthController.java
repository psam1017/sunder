package portfolio.sunder.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import portfolio.sunder.domain.student.dto.request.StudentSave;
import portfolio.sunder.domain.student.service.StudentService;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {

    @Secured("USER_STUDENT")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
