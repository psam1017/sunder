package psam.portfolio.sunder.english.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TeacherCommandServiceTest extends SunderApplicationTests {

    @Autowired
    TeacherCommandService sut;

    @Autowired
    TeacherQueryRepository teacherQueryRepository;

    @Autowired
    PasswordUtils passwordUtils;

    @DisplayName("선생님이 선생님을 등록할 수 있다.")
    @Test
    void registerTeacher(){
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, RoleName.ROLE_DIRECTOR, RoleName.ROLE_TEACHER);

        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getRawPassword();
        String name = "name";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();

        TeacherPOST post = new TeacherPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode()
        );

        // when
        UUID saveTeacherId = refreshAnd(() -> sut.register(director.getUuid(), post));

        // then
        Teacher getTeacher = teacherQueryRepository.getById(saveTeacherId);
        assertThat(getTeacher.getLoginId()).isEqualTo(loginId);
        assertThat(passwordUtils.matches(password, getTeacher.getLoginPw())).isTrue();
        assertThat(getTeacher.getName()).isEqualTo(name);
        assertThat(getTeacher.getEmail()).isEqualTo(email);
        assertThat(getTeacher.getPhone()).isEqualTo(phoneNumber);
        assertThat(getTeacher.getAddress().getStreet()).isEqualTo(address.getStreet());
        assertThat(getTeacher.getAddress().getDetail()).isEqualTo(address.getDetail());
        assertThat(getTeacher.getAddress().getPostalCode()).isEqualTo(address.getPostalCode());

        assertThat(getTeacher.getAcademy().getUuid()).isEqualTo(academy.getUuid());
        assertThat(getTeacher.getRoles()).hasSize(1)
                .extracting(UserRole::getRoleName)
                .containsOnly(RoleName.ROLE_TEACHER);
    }
}
