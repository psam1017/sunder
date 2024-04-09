package psam.portfolio.sunder.english.domain.teacher.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
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
public class TeacherFullResponse {

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
    private UUID academyId;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;
    private UUID createdBy;
    private UUID modifiedBy;

    public static TeacherFullResponse from(Teacher teacher) {
        return TeacherFullResponse.builder()
                .id(teacher.getId())
                .loginId(teacher.getLoginId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .emailVerified(teacher.isEmailVerified())
                .phone(teacher.getPhone())
                .street(teacher.getAddress().getStreet())
                .addressDetail(teacher.getAddress().getDetail())
                .postalCode(teacher.getAddress().getPostalCode())
                .status(teacher.getStatus())
                .roles(teacher.getRoles().stream().map(UserRole::getRoleName).toList())
                .lastPasswordChangeDateTime(teacher.getLastPasswordChangeDateTime())
                .academyId(teacher.getAcademy().getId())
                .createdDateTime(teacher.getCreatedDateTime())
                .modifiedDateTime(teacher.getModifiedDateTime())
                .createdBy(teacher.getCreatedBy())
                .modifiedBy(teacher.getModifiedBy())
                .build();
    }
}
