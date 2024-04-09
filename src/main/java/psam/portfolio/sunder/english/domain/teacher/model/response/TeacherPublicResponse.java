package psam.portfolio.sunder.english.domain.teacher.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class TeacherPublicResponse {

    private UUID id;
    private String name;
    private UserStatus status;
    private List<RoleName> roles;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;

    public static TeacherPublicResponse from(Teacher teacher) {
        return TeacherPublicResponse.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .status(teacher.getStatus())
                .roles(teacher.getRoles().stream().map(UserRole::getRoleName).toList())
                .createdDateTime(teacher.getCreatedDateTime())
                .modifiedDateTime(teacher.getModifiedDateTime())
                .build();
    }
}
