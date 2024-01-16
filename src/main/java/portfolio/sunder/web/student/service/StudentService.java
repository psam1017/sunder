package portfolio.sunder.web.student.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.sunder.web.student.dto.request.StudentSave;
import portfolio.sunder.web.student.dto.response.StudentResponse;
import portfolio.sunder.web.student.entity.Student;
import portfolio.sunder.web.student.repository.StudentCommandRepository;
import portfolio.sunder.web.student.repository.StudentQueryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class StudentService {

    private final StudentCommandRepository studentCommandRepository;
    private final StudentQueryRepository studentQueryRepository;

    public long register(StudentSave student) {
        Student saveStudent = studentCommandRepository.save(student.toEntity());
        return saveStudent.getId();
    }

    public StudentResponse find(long id) {
        // TODO: 2024-01-13 getById
        Student student = studentQueryRepository.findById(id).orElseThrow();
        return new StudentResponse(student);
    }
}
