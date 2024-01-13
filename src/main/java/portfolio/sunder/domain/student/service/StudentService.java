package portfolio.sunder.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio.sunder.domain.student.dto.request.StudentSave;
import portfolio.sunder.domain.student.dto.response.StudentResponse;
import portfolio.sunder.domain.student.entity.Student;
import portfolio.sunder.domain.student.repository.StudentCommandRepository;
import portfolio.sunder.domain.student.repository.StudentQueryRepository;

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
