package portfolio.sunder.web.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.sunder.web.student.entity.Student;

public interface StudentCommandRepository extends JpaRepository<Student, Long> {
}
