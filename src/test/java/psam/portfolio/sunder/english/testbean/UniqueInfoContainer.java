package psam.portfolio.sunder.english.testbean;

import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;

public interface UniqueInfoContainer {

    String getUniqueLoginId();
    String getUniqueEmail();
    String getUniquePhoneNumber();
    String getUniqueAcademyName();
    String getUniqueAttendanceId();

    default Address getAnyAddress() {
        return Address.builder()
                .street("서울특별시 영등포구 의사당대로 1")
                .detail("국회")
                .postalCode("07233")
                .build();
    }

    default School getAnySchool() {
        return School.builder()
                .schoolName("선더초등학교")
                .grade(3)
                .build();
    }

    default Parent getAnyParent() {
        return Parent.builder()
                .parentName("홍길동")
                .parentPhone("010-1234-5678")
                .build();
    }
}
