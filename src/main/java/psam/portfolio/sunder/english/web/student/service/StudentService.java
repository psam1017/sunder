package psam.portfolio.sunder.english.web.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.student.dto.request.StudentSave;
import psam.portfolio.sunder.english.web.student.dto.response.StudentResponse;
import psam.portfolio.sunder.english.web.student.entity.Student;
import psam.portfolio.sunder.english.web.student.repository.StudentCommandRepository;
import psam.portfolio.sunder.english.web.student.repository.StudentQueryRepository;

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
