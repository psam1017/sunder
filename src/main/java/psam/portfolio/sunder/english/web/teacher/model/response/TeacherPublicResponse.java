package psam.portfolio.sunder.english.web.teacher.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class TeacherPublicResponse {

    private String name;
    private UserStatus status;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;

    public static TeacherPublicResponse from(Teacher teacher) {
        return TeacherPublicResponse.builder()
                .name(teacher.getName())
                .status(teacher.getStatus())
                .createdDateTime(teacher.getCreatedDateTime())
                .modifiedDateTime(teacher.getModifiedDateTime())
                .build();
    }
}
