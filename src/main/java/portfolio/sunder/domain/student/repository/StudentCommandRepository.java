package portfolio.sunder.domain.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.sunder.domain.student.entity.Student;

public interface StudentCommandRepository extends JpaRepository<Student, Long> {
}
