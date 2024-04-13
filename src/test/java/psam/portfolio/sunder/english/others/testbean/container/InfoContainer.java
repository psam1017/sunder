package psam.portfolio.sunder.english.others.testbean.container;

import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;

import java.util.UUID;

public interface InfoContainer {

    String getUniqueLoginId();
    String getUniqueEmail();
    String getUniquePhoneNumber();
    String getUniqueAcademyName();
    String getUniqueAttendanceId();
    default String getUniqueWordEnglish() {
        return "eng" + UUID.randomUUID().toString().substring(0, 8);
    };
    default String getUniqueWordKorean() {
        return "한" + UUID.randomUUID().toString().substring(0, 8);
    };

    default String getAnyRawPassword() {
        return "qwe123!@#";
    }

    default Address getAnyAddress() {
        return Address.builder()
                .street("서울특별시 선더구 선더로 1")
                .detail("선더빌딩")
                .postalCode("00000")
                .build();
    }

    default School getAnySchool() {
        return School.builder()
                .name("선더초등학교")
                .grade(3)
                .build();
    }

    default Parent getAnyParent() {
        return Parent.builder()
                .name("선더부모")
                .phone("01012345678")
                .build();
    }
}
