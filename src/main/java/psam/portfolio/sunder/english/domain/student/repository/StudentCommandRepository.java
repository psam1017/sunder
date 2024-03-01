package psam.portfolio.sunder.english.domain.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;

public interface StudentCommandRepository extends JpaRepository<Student, Long> {
}
