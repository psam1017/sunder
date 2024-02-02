package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.SunderApplicationTests;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.service.AcademyQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AcademyQueryServiceTest extends SunderApplicationTests {

    @Autowired
    AcademyQueryService sut; // system under test

    @DisplayName("학원의 정보 중복 검사를 할 때는 하나의 데이터만 전달해야 한다.")
    @Test
    void oneParamToCheckDuplException(){
        // given
        String name = "name";
        String phone = "";
        String email = "email";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원의 정보 중복 검사를 할 때는 아이디, 이메일, 연락처 중 하나는 반드시 전달해야 한다.")
    @Test
    void noParamToCheckDuplException(){
        // given
        String name = "";
        String phone = "";
        String email = "";

        // when
        // then
        assertThatThrownBy(() -> refreshAnd(() -> sut.checkDuplication(name, phone, email)))
                .isInstanceOf(OneParamToCheckAcademyDuplException.class);
    }

    @DisplayName("학원 이름의 중복 검사를 수행할 수 있다.")
    @Test
    void checkNameDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = registerAcademy.getName();
        String phone = null;
        String email = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 연락처 중복 검사를 수행할 수 있다.")
    @Test
    void checkPhoneDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = registerAcademy.getPhone();
        String email = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("학원의 이메일 중복 검사를 수행할 수 있다.")
    @Test
    void checkEmailDupl(){
        // given
        Academy registerAcademy = registerAcademy(AcademyStatus.VERIFIED);

        String name = null;
        String phone = null;
        String email = registerAcademy.getEmail();

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isFalse();
    }

    @DisplayName("PENDING 상태의 학원은 중복 검사에서 제외된다.")
    @Test
    void ifPendingOk(){
        // given
        Academy academy = registerAcademy(AcademyStatus.PENDING);

        String name = academy.getName();
        String email = null;
        String phone = null;

        // when
        boolean isOk = refreshAnd(() -> sut.checkDuplication(name, phone, email));

        // then
        assertThat(isOk).isTrue();
    }
}
