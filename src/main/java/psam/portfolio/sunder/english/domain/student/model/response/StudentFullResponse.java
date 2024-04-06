package psam.portfolio.sunder.english.domain.student.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class StudentFullResponse {

    private UUID id;
    private String loginId;
    private String name;
    private String email;
    private Boolean emailVerified;
    private String phone;
    private String street;
    private String addressDetail;
    private String postalCode;
    private UserStatus status;
    private List<RoleName> roles;
    @KoreanDateTime
    private LocalDateTime lastPasswordChangeDateTime;
    private String attendanceId;
    private String note;
    private String schoolName;
    private Integer schoolGrade;
    private String parentName;
    private String parentPhone;
    private UUID academyId;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;
    private UUID createdBy;
    private UUID modifiedBy;

    public static StudentFullResponse from(Student student, boolean displayNote) {
        return StudentFullResponse.builder()
                .id(student.getUuid())
                .loginId(student.getLoginId())
                .name(student.getName())
                .email(student.getEmail())
                .emailVerified(student.isEmailVerified())
                .phone(student.getPhone())
                .street(student.getAddress().getStreet())
                .addressDetail(student.getAddress().getDetail())
                .postalCode(student.getAddress().getPostalCode())
                .status(student.getStatus())
                .lastPasswordChangeDateTime(student.getLastPasswordChangeDateTime())
                .roles(student.getRoles().stream().map(UserRole::getRoleName).toList())
                .attendanceId(student.getAttendanceId())
                .note(displayNote ? student.getNote() : "")
                .schoolName(student.getSchool().getName())
                .schoolGrade(student.getSchool().getGrade())
                .parentName(student.getParent().getName())
                .parentPhone(student.getParent().getPhone())
                .academyId(student.getAcademy().getUuid())
                .createdDateTime(student.getCreatedDateTime())
                .modifiedDateTime(student.getModifiedDateTime())
                .createdBy(student.getCreatedBy())
                .modifiedBy(student.getModifiedBy())
                .build();
    }
}
