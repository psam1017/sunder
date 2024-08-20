package psam.portfolio.sunder.english.others.dummy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;

// profile 을 stg 로 설정하고 사용
@Disabled
public class DummyDataTest extends AbstractSunderApplicationTest {

    // 스테이징 DB 를 만들 때 잊지 말고 utf8 설정을 해주도록 한다.
    // create database sunder_stg character set utf8 collate utf8_general_ci;
//    @Commit
    @DisplayName("더미 데이터를 직접 stg 서버에 올린다.")
    @Test
    public void insertDummyData() {
        for (int i = 0; i < 230; i++) {
            Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
            Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
            dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
            Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
            dataCreator.createUserRoles(teacher, ROLE_TEACHER);
            Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
            dataCreator.createUserRoles(student, ROLE_STUDENT);
            Book book = dataCreator.registerAnyBook(academy);
            for (int j = 1; j <= 11; j++) {
                dataCreator.registerWord("apple" + j, "사과" + j, book);
            }
            refresh();
        }
    }
}
