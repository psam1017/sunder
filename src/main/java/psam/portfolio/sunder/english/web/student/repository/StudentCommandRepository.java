package psam.portfolio.sunder.english.web.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.web.student.model.entity.Student;

public interface StudentCommandRepository extends JpaRepository<Student, Long> {
}
