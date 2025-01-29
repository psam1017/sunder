package psam.portfolio.sunder.english.others.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.student.scheduler.StudentScheduler;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import static org.assertj.core.api.Assertions.assertThat;

class StudentSchedulerTest extends AbstractSunderApplicationTest {

    @Autowired
    StudentScheduler sut;

    @Autowired
    StudentQueryRepository studentQueryRepository;

    @DisplayName("매년 초에 고3 이전의 학생들의 학년을 올릴 수 있다.")
    @Test
    void increaseGrade() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Student student1 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        student1.getSchool().setGrade(1);
        Student student11 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        student11.getSchool().setGrade(11);
        Student student12 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        student12.getSchool().setGrade(12);

        // when
        refreshAnd(() -> sut.increaseGrade());

        // then
        Student student1After = studentQueryRepository.getById(student1.getId());
        assertThat(student1After.getSchool().getGrade()).isEqualTo(2);
        Student student11After = studentQueryRepository.getById(student11.getId());
        assertThat(student11After.getSchool().getGrade()).isEqualTo(12);
        Student student12After = studentQueryRepository.getById(student12.getId());
        assertThat(student12After.getSchool().getGrade()).isEqualTo(12);
    }
}