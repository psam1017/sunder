package psam.portfolio.sunder.english.domain.student.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class StudentPublicResponse {

    private UUID id;
    private String loginId;
    private String name;
    private String email;
    private String phone;
    private String street;
    private String addressDetail;
    private String postalCode;
    private UserStatus status;
    private String attendanceId;
    private String schoolName;
    private Integer schoolGrade;
    private String parentName;
    private String parentPhone;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;
    private UUID createdBy;
    private UUID modifiedBy;

   public static StudentPublicResponse from(Student student) {
        return StudentPublicResponse.builder()
                .id(student.getId())
                .loginId(student.getLoginId())
                .name(student.getName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .street(student.getAddress() != null ? student.getAddress().getStreet() : null)
                .addressDetail(student.getAddress() != null ? student.getAddress().getDetail() : null)
                .postalCode(student.getAddress() != null ? student.getAddress().getPostalCode() : null)
                .status(student.getStatus())
                .attendanceId(student.getAttendanceId())
                .schoolName(student.getSchool().getName())
                .schoolGrade(student.getSchool().getGrade())
                .parentName(student.getParent().getName())
                .parentPhone(student.getParent().getPhone())
                .createdDateTime(student.getCreatedDateTime())
                .modifiedDateTime(student.getModifiedDateTime())
                .createdBy(student.getCreatedBy())
                .modifiedBy(student.getModifiedBy())
                .build();
    }
}
